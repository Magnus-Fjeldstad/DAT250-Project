package com.example.pollapp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOptionDto {
    private Long id;
    private String caption;
    private Integer presentationOrder;
}
