package com.example.pollapp.mapper;

import com.example.pollapp.domain.Poll;
import com.example.pollapp.domain.VoteOption;
import com.example.pollapp.dto.VoteOptionDto;
import org.springframework.stereotype.Component;

@Component
public class VoteOptionMapper {

    // Entity -> DTO
    public VoteOptionDto toDto(VoteOption opt) {
        return VoteOptionDto.builder()
                .id(opt.getId())
                .caption(opt.getCaption())
                .presentationOrder(opt.getPresentationOrder())
                .build();
    }

    // DTO -> Entity (without poll)
    public VoteOption toEntity(VoteOptionDto dto) {
        VoteOption opt = new VoteOption();
        opt.setId(dto.getId());
        opt.setCaption(dto.getCaption());
        opt.setPresentationOrder(dto.getPresentationOrder() == null ? 0 : dto.getPresentationOrder());
        return opt;
    }

    // Attach poll reference after creation
    public void attachPoll(VoteOption opt, Poll poll) {
        opt.setPoll(poll);
    }
}
