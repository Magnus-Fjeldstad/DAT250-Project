package com.example.pollapp.service;

import com.example.pollapp.domain.Poll;
import com.example.pollapp.domain.User;
import com.example.pollapp.domain.VoteOption;
import com.example.pollapp.dto.PollDto;
import com.example.pollapp.dto.PollResultDto;
import com.example.pollapp.mapper.PollMapper;
import com.example.pollapp.repo.PollRepository;
import com.example.pollapp.repo.VoteOptionRepository;
import com.example.pollapp.repo.VoteRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

// PollService with role-aware rules:
// - GUEST: can only GET (enforced by SecurityConfig)
// - USER: can create, and update/delete ONLY own polls
// - ADMIN: can create/update/delete ANY poll
@Service
public class PollService {

    private final PollRepository pollRepository;
    private final PollResultCacheService cache;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRepository voteRepository;
    private final AuthService authService;
    private final PollMapper pollMapper;

    public PollService(
            PollRepository pollRepository,
            PollResultCacheService cache,
            VoteOptionRepository voteOptionRepository,
            VoteRepository voteRepository,
            AuthService authService,
            PollMapper pollMapper,
            SimpMessagingTemplate messagingTemplate // add this
    ) {
        this.pollRepository = pollRepository;
        this.cache = cache;
        this.voteOptionRepository = voteOptionRepository;
        this.voteRepository = voteRepository;
        this.authService = authService;
        this.pollMapper = pollMapper;
    }

    /* ---------- Public API (read) ---------- */

    // Everyone (including GUEST) can list polls (permitted in SecurityConfig)
    public List<PollDto> findAll() {
        return pollRepository.findAll().stream()
                .map(pollMapper::toDto)
                .toList();
    }

    // Everyone can read a specific poll (permitted in SecurityConfig)
    public Optional<PollDto> findById(Long id) {
        return pollRepository.findById(id).map(pollMapper::toDto);
    }

    /* ---------- Public API (write) ---------- */

    // CREATE: creator must be the current session user (ignore dto.creatorId for security)
    public PollDto create(PollDto dto) {
        User creator = authService.getCurrentUser();
        Poll poll = new Poll();
        pollMapper.applyDtoToEntity(dto, poll, creator, true);
        Poll saved = pollRepository.save(poll);
        return pollMapper.toDto(saved);
    }


    // UPDATE: ADMIN can update any poll; USER only own polls. Creator cannot be reassigned by client.
    public Optional<PollDto> update(Long id, PollDto dto) {
        User current = authService.getCurrentUser();

        return pollRepository.findById(id).map(existing -> {
            User creator = existing.getCreator();

            // Enforce ownership or ADMIN privilege
            if (!authService.isAdmin(current)) {
                if (creator == null || !creator.getId().equals(current.getId())) {
                    throw new ResponseStatusException(FORBIDDEN, "You cannot edit this poll");
                }
            }

            // Do not allow changing creator via DTO
            if (dto.getCreatorId() != null && !Objects.equals(dto.getCreatorId(), creator != null ? creator.getId() : null)) {
                throw new ResponseStatusException(FORBIDDEN, "Creator cannot be changed");
            }

            pollMapper.applyDtoToEntity(dto, existing, creator, false); // false = update; replace options if provided
            Poll saved = pollRepository.save(existing);
            return pollMapper.toDto(saved);
        });
    }

    // DELETE: ADMIN can delete any poll; USER only own polls
    public void delete(Long id) {
        User current = authService.getCurrentUser();
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Poll not found"));

        if (!authService.isAdmin(current)) {
            User creator = poll.getCreator();
            if (creator == null || !creator.getId().equals(current.getId())) {
                throw new ResponseStatusException(FORBIDDEN, "You cannot delete this poll");
            }
        }

        pollRepository.delete(poll);
    }


    // Results are readable by everyone (GUEST allowed)
    public List<PollResultDto> getResults(Long pollId) {
        var cached = cache.loadResults(pollId);
        if (cached.isPresent()) return cached.get();

        List<VoteOption> options = voteOptionRepository.findByPollIdOrderByPresentationOrderAsc(pollId);

        List<Object[]> rows = voteRepository.aggregateUpDownByPoll(pollId);
        Map<Long, int[]> countsByOpt = new HashMap<>();
        for (Object[] r : rows) {
            Long optionId = (Long) r[0];
            Number up = (Number) r[1];
            Number down = (Number) r[2];
            countsByOpt.put(optionId, new int[]{
                    up == null ? 0 : up.intValue(),
                    down == null ? 0 : down.intValue()
            });
        }

        List<PollResultDto> results = new ArrayList<>();
        Map<Long, Integer> orders = new HashMap<>();
        for (VoteOption opt : options) {
            int[] c = countsByOpt.getOrDefault(opt.getId(), new int[]{0, 0});
            results.add(PollResultDto.builder()
                    .optionId(opt.getId())
                    .caption(opt.getCaption())
                    .upVotes(c[0])
                    .downVotes(c[1])
                    .build());
            orders.put(opt.getId(), opt.getPresentationOrder());
        }

        cache.saveResults(pollId, results, orders);
        return results;
    }
}
