package com.example.pollapp.mapper;

import com.example.pollapp.domain.Vote;
import com.example.pollapp.dto.VoteDto;
import org.springframework.stereotype.Component;

@Component
public class VoteMapper {

    public VoteDto toDto(Vote v) {
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
