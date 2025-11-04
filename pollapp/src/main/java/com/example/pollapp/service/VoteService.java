package com.example.pollapp.service;

import com.example.pollapp.domain.User;
import com.example.pollapp.domain.Vote;
import com.example.pollapp.domain.VoteOption;
import com.example.pollapp.dto.VoteDto;
import com.example.pollapp.repo.VoteOptionRepository;
import com.example.pollapp.repo.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.example.pollapp.config.RabbitConfig.VOTE_QUEUE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final PollResultCacheService cache;
    private final AuthService authService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate; // Rabbit producer

    public VoteService(
            VoteRepository voteRepository,
            VoteOptionRepository voteOptionRepository,
            PollResultCacheService cache,
            AuthService authService,
            SimpMessagingTemplate messagingTemplate,
            RabbitTemplate rabbitTemplate // inject RabbitTemplate
    ) {
        this.voteRepository = voteRepository;
        this.voteOptionRepository = voteOptionRepository;
        this.cache = cache;
        this.authService = authService;
        this.messagingTemplate = messagingTemplate;
        this.rabbitTemplate = rabbitTemplate;
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

        // Check for existing vote
        Optional<Vote> maybeExisting =
                voteRepository.findByVoterIdAndVotedOnId(user.getId(), option.getId());

        // Remove vote (value = 0)
        if (cmd.getValue() == 0) {
            maybeExisting.ifPresent(v -> {
                voteRepository.delete(v);
                cache.evict(pollId);
                broadcastPollUpdate(pollId);
                sendVoteEvent(cmd); // publish delete event
            });
            return Optional.empty();
        }

        // Update existing
        if (maybeExisting.isPresent()) {
            Vote existing = maybeExisting.get();

            if (existing.getValue() == cmd.getValue()) {
                voteRepository.delete(existing);
                cache.evict(pollId);
                broadcastPollUpdate(pollId);
                sendVoteEvent(cmd); // publish delete event
                return Optional.empty();
            } else {
                existing.setValue(cmd.getValue());
                existing.setPublishedAt(Instant.now());
                Vote saved = voteRepository.save(existing);
                cache.evict(pollId);
                broadcastPollUpdate(pollId);
                VoteDto dto = toDto(saved);
                sendVoteEvent(dto); // publish update event
                return Optional.of(dto);
            }
        }

        // New vote
        Vote v = new Vote();
        v.setVoter(user);
        v.setVotedOn(option);
        v.setValue(cmd.getValue());
        v.setPublishedAt(Instant.now());
        Vote saved = voteRepository.save(v);

        cache.evict(pollId);
        broadcastPollUpdate(pollId);

        VoteDto dto = toDto(saved);
        sendVoteEvent(dto); // publish create event
        return Optional.of(dto);
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
        if (value == null) {
            throw new ResponseStatusException(BAD_REQUEST, "value missing in request");
        }
        if (!(value == -1 || value == 0 || value == 1)) {
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

    private void broadcastPollUpdate(Long pollId) {
        var votes = voteRepository.findAllByVotedOn_Poll_Id(pollId)
                .stream()
                .map(this::toDto)
                .toList();

        messagingTemplate.convertAndSend("/topic/poll/" + pollId, votes);
    }

    /* ---------- RabbitMQ publish ---------- */
    private void sendVoteEvent(VoteDto dto) {
        if (dto == null) return;
        rabbitTemplate.convertAndSend(VOTE_QUEUE, dto); // send JSON message
    }
}
