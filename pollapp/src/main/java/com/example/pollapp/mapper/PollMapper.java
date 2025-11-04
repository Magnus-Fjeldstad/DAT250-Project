package com.example.pollapp.mapper;

import com.example.pollapp.domain.Poll;
import com.example.pollapp.domain.User;
import com.example.pollapp.domain.VoteOption;
import com.example.pollapp.dto.PollDto;
import com.example.pollapp.dto.VoteOptionDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PollMapper {

    private final VoteOptionMapper voteOptionMapper;

    public PollMapper(VoteOptionMapper voteOptionMapper) {
        this.voteOptionMapper = voteOptionMapper;
    }

    public PollDto toDto(Poll poll) {
        return PollDto.builder()
                .id(poll.getId())
                .question(poll.getQuestion())
                .publishedAt(poll.getPublishedAt())
                .validUntil(poll.getValidUntil())
                .creatorId(poll.getCreator() != null ? poll.getCreator().getId() : null)
                .options(poll.getOptions() == null ? List.of() :
                        poll.getOptions().stream()
                                .map(voteOptionMapper::toDto)
                                .toList())
                .build();
    }

    public void applyDtoToEntity(PollDto dto, Poll poll, User creator, boolean isCreate) {
        if (dto.getQuestion() != null) poll.setQuestion(dto.getQuestion());
        if (dto.getPublishedAt() != null) poll.setPublishedAt(dto.getPublishedAt());
        if (dto.getValidUntil() != null) poll.setValidUntil(dto.getValidUntil());
        poll.setCreator(creator);

        if (isCreate || dto.getOptions() != null) {
            List<VoteOption> newOptions = new ArrayList<>();

            if (dto.getOptions() != null) {
                for (VoteOptionDto o : dto.getOptions()) {
                    VoteOption opt = voteOptionMapper.toEntity(o);
                    voteOptionMapper.attachPoll(opt, poll);
                    newOptions.add(opt);
                }
            }

            poll.getOptions().clear();
            poll.getOptions().addAll(newOptions);
        }
    }
}
