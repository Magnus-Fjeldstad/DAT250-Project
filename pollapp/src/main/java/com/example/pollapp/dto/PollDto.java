package com.example.pollapp.dto;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollDto {
    private Long id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    private Long creatorId;
    private String userName;
    private List<VoteOptionDto> options;
}
