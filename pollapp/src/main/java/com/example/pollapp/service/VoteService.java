package com.example.pollapp.service;

import com.example.pollapp.domain.User;
import com.example.pollapp.domain.Vote;
import com.example.pollapp.domain.VoteOption;
import com.example.pollapp.dto.VoteDto;
import com.example.pollapp.mapper.VoteMapper;
import com.example.pollapp.repo.VoteOptionRepository;
import com.example.pollapp.repo.VoteRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.example.pollapp.config.RabbitConfig.VOTE_QUEUE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final PollResultCacheService cache;
    private final AuthService authService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate; // Rabbit producer
    private final VoteMapper voteMapper;

    public VoteService(
            VoteRepository voteRepository,
            VoteOptionRepository voteOptionRepository,
            PollResultCacheService cache,
            AuthService authService,
            SimpMessagingTemplate messagingTemplate,
            RabbitTemplate rabbitTemplate,
            VoteMapper voteMapper
    ) {
        this.voteRepository = voteRepository;
        this.voteOptionRepository = voteOptionRepository;
        this.cache = cache;
        this.authService = authService;
        this.messagingTemplate = messagingTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.voteMapper = voteMapper;
    }

    public Optional<VoteDto> vote(VoteDto cmd) {
        // Must be logged in
        User user = authService.getCurrentUser();

        validateValue(cmd.getValue());

        // Lookup option
        Long optionId = require(cmd.getOptionId());
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
                return getVoteDto(cmd, pollId, existing);
            }
        }

        // New vote
        Vote v = new Vote();
        v.setVoter(user);
        v.setVotedOn(option);
        return getVoteDto(cmd, pollId, v);
    }



    /* ---------- Queries ---------- */

    public List<VoteDto> findAll() {
        return voteRepository.findAll().stream().map(voteMapper::toDto).toList();
    }

    public List<VoteDto> findByUserId(Long userId) {
        return voteRepository.findAllByVoterId(userId).stream().map(voteMapper::toDto).toList();
    }

    public List<VoteDto> findByPollId(Long pollId) {
        return voteRepository.findAllByVotedOn_Poll_Id(pollId).stream().map(voteMapper::toDto).toList();
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


    private Long require(Long v) {
        if (v == null) throw new ResponseStatusException(BAD_REQUEST, "optionId" + " is required");
        return v;
    }


    private void broadcastPollUpdate(Long pollId) {
        var votes = voteRepository.findAllByVotedOn_Poll_Id(pollId)
                .stream()
                .map(voteMapper::toDto)
                .toList();

        messagingTemplate.convertAndSend("/topic/poll/" + pollId, votes);
    }

    private Optional<VoteDto> getVoteDto(VoteDto cmd, Long pollId, Vote v) {
        v.setValue(cmd.getValue());
        v.setPublishedAt(Instant.now());
        Vote saved = voteRepository.save(v);

        cache.evict(pollId);
        broadcastPollUpdate(pollId);

        VoteDto dto = voteMapper.toDto(saved);
        sendVoteEvent(dto);
        return Optional.of(dto);
    }

    /* ---------- RabbitMQ publish ---------- */
    private void sendVoteEvent(VoteDto dto) {
        if (dto == null) return;
        rabbitTemplate.convertAndSend(VOTE_QUEUE, dto); // send JSON message
    }
}
