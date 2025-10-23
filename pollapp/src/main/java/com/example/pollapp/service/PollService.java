package com.example.pollapp.service;

import com.example.pollapp.domain.Poll;
import com.example.pollapp.domain.User;
import com.example.pollapp.domain.VoteOption;
import com.example.pollapp.dto.PollDto;
import com.example.pollapp.dto.PollResultDto;
import com.example.pollapp.dto.VoteOptionDto;
import com.example.pollapp.repo.PollRepository;
import com.example.pollapp.repo.UserRepo;
import com.example.pollapp.repo.VoteOptionRepository;
import com.example.pollapp.repo.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.http.HttpStatus.*;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final UserRepo userRepository;
    private final PollResultCacheService cache;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRepository voteRepository;

    public PollService(PollRepository pollRepository, UserRepo userRepository, PollResultCacheService cache, VoteOptionRepository voteOptionRepository, VoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.cache = cache;
        this.voteOptionRepository = voteOptionRepository;
        this.voteRepository = voteRepository;
    }

    /* ---------- Public API ---------- */

    public List<PollDto> findAll() {
        return pollRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<PollDto> findById(Long id) {
        return pollRepository.findById(id).map(this::toDto);
    }

    public PollDto create(PollDto dto) {
        if (dto.getCreatorId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "creatorId is required");
        }
        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "creatorId not found"));

        Poll poll = new Poll();
        applyDtoToEntity(dto, poll, creator, true); // true = ny poll, bygg options
        Poll saved = pollRepository.save(poll);
        return toDto(saved);
    }

    public Optional<PollDto> update(Long id, PollDto dto) {
        return pollRepository.findById(id).map(existing -> {
            User creator = existing.getCreator();
            if (dto.getCreatorId() != null && (creator == null || !dto.getCreatorId().equals(creator.getId()))) {
                creator = userRepository.findById(dto.getCreatorId())
                        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "creatorId not found"));
            }
            applyDtoToEntity(dto, existing, creator, false); // false = oppdater, erstatt options om gitt
            Poll saved = pollRepository.save(existing);
            return toDto(saved);
        });
    }

    public void delete(Long id) {
        if (!pollRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Poll not found");
        }
        pollRepository.deleteById(id);
    }

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
            countsByOpt.put(optionId, new int[]{ up == null ? 0 : up.intValue(), down == null ? 0 : down.intValue() });
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

    /* ---------- Mapping ---------- */

    private PollDto toDto(Poll poll) {
        return PollDto.builder()
                .id(poll.getId())
                .question(poll.getQuestion())
                .publishedAt(poll.getPublishedAt())
                .validUntil(poll.getValidUntil())
                .creatorId(poll.getCreator() != null ? poll.getCreator().getId() : null)
                .options(poll.getOptions() == null ? List.of() :
                        poll.getOptions().stream()
                                .map(opt -> VoteOptionDto.builder()
                                        .id(opt.getId())
                                        .caption(opt.getCaption())
                                        .presentationOrder(opt.getPresentationOrder())
                                        .build())
                                .toList())
                .build();
    }

    /**
     * Kopierer felter/opsjoner fra DTO til entity.
     * Hvis isCreate = true, bygges en helt ny options-liste.
     * Hvis isCreate = false:
     *   - hvis dto.options != null, erstattes hele options-listen (orphanRemoval på Poll sørger for opprydding)
     *   - hvis dto.options == null, behold eksisterende options uendret
     */
    private void applyDtoToEntity(PollDto dto, Poll poll, User creator, boolean isCreate) {
        if (dto.getQuestion() != null) poll.setQuestion(dto.getQuestion());
        if (dto.getPublishedAt() != null) poll.setPublishedAt(dto.getPublishedAt());
        if (dto.getValidUntil() != null) poll.setValidUntil(dto.getValidUntil());
        poll.setCreator(creator);

        if (isCreate || dto.getOptions() != null) {
            // Erstatt hele options-listen basert på DTO
            List<VoteOption> newOptions = new ArrayList<>();
            if (dto.getOptions() != null) {
                for (VoteOptionDto o : dto.getOptions()) {
                    VoteOption opt = new VoteOption();
                    opt.setId(o.getId()); // ignorert ved insert (auto gen), men ok ved ev. merge
                    opt.setCaption(o.getCaption());
                    opt.setPresentationOrder(o.getPresentationOrder() == null ? 0 : o.getPresentationOrder());
                    opt.setPoll(poll); // backref
                    newOptions.add(opt);
                }
            }
            poll.getOptions().clear();
            poll.getOptions().addAll(newOptions);
        }
    }
}
