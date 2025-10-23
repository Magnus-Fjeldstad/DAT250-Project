package com.example.pollapp.dto;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteDto {
    private Long id;
    private Long userId;
    private Long optionId;
    private Long pollId;
    private int value;
    private Instant publishedAt;
}
