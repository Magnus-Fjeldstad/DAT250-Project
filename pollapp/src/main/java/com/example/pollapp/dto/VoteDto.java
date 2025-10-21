package com.example.pollapp.dto;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteDto {
    private Long id;           // null ved POST
    private Long userId;
    private Long optionId;
    private Long pollId;       // utledes fra option → poll
    private int value;         // 1, -1 eller 0 (0 = fjern)
    private Instant publishedAt;
}
