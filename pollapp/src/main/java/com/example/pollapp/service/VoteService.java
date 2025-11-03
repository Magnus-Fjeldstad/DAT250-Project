package com.example.pollapp.service;

import com.example.pollapp.domain.User;
import com.example.pollapp.domain.Vote;
import com.example.pollapp.domain.VoteOption;
import com.example.pollapp.dto.VoteDto;
import com.example.pollapp.repo.VoteOptionRepository;
import com.example.pollapp.repo.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final PollResultCacheService cache;
    private final AuthService authService;
    
    public VoteService(
            VoteRepository voteRepository,
            VoteOptionRepository voteOptionRepository,
            PollResultCacheService cache,
            AuthService authService
    ) {
        this.voteRepository = voteRepository;
        this.voteOptionRepository = voteOptionRepository;
        this.cache = cache;
        this.authService = authService;
    }

    public Optional<VoteDto> vote(VoteDto cmd) {
        // Must be logged in
        User user = authService.getCurrentUser();

        validateValue(cmd.getValue());

        // Lookup option
        Long optionId = require(cmd.getOptionId(), "optionId");
        VoteOption option = voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "optionId not found"));

        Long pollId = option.getPoll().getId();

        // Existing?
        Optional<Vote> maybeExisting =
                voteRepository.findByVoterIdAndVotedOnId(user.getId(), option.getId());

        // Remove if 0
        if (cmd.getValue() == 0) {
            maybeExisting.ifPresent(v -> {
                log.info("Vote removed: user={} option={} oldValue={}", user.getId(), option.getId(), v.getValue());
                voteRepository.delete(v);
            });
            cache.evict(pollId);
            return Optional.empty();
        }

        // Toggle/update logic
        if (maybeExisting.isPresent()) {
            Vote existing = maybeExisting.get();
            if (existing.getValue() == cmd.getValue()) {
                // Toggle off
                log.info("Vote toggled off: user={} option={} value={}", user.getId(), option.getId(), existing.getValue());
                voteRepository.delete(existing);
                cache.evict(pollId);
                return Optional.empty();
            } else {
                // Update
                log.info("Vote updated: user={} option={} old={} new={}", user.getId(), option.getId(), existing.getValue(), cmd.getValue());
                existing.setValue(cmd.getValue());
                existing.setPublishedAt(Instant.now());
                Vote saved = voteRepository.save(existing);
                cache.evict(pollId);
                return Optional.of(toDto(saved));
            }
        }

        // New vote
        log.info("Vote created: user={} option={} value={}", user.getId(), option.getId(), cmd.getValue());
        Vote v = new Vote();
        v.setVoter(user);
        v.setVotedOn(option);
        v.setValue(cmd.getValue());
        v.setPublishedAt(Instant.now());
        Vote saved = voteRepository.save(v);

        cache.evict(pollId);
        return Optional.of(toDto(saved));
    }

    /* ---------- Queries ---------- */

    public List<VoteDto> findAll() {
        return voteRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<VoteDto> findByUserId(Long userId) {
        return voteRepository.findAllByVoterId(userId).stream().map(this::toDto).toList();
    }

    public List<VoteDto> findByPollId(Long pollId) {
        return voteRepository.findAllByVotedOn_Poll_Id(pollId).stream().map(this::toDto).toList();
    }

    /* ---------- Helpers ---------- */

    private void validateValue(Integer value) {
        if (value == null || !(value == -1 || value == 0 || value == 1)) {
            throw new ResponseStatusException(BAD_REQUEST, "value must be one of -1, 0, 1");
        }
    }

    private Long require(Long v, String name) {
        if (v == null) throw new ResponseStatusException(BAD_REQUEST, name + " is required");
        return v;
    }

    private VoteDto toDto(Vote v) {
        Long pollId = (v.getVotedOn() != null && v.getVotedOn().getPoll() != null)
                ? v.getVotedOn().getPoll().getId() : null;

        return VoteDto.builder()
                .id(v.getId())
                .userId(v.getVoter() != null ? v.getVoter().getId() : null)
                .optionId(v.getVotedOn() != null ? v.getVotedOn().getId() : null)
                .pollId(pollId)
                .value(v.getValue())
                .publishedAt(v.getPublishedAt())
                .build();
    }
}
