package com.example.pollapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollResultDto {
    private Long optionId;
    private String caption;
    private Integer upVotes;
    private Integer downVotes;

    @JsonProperty("score")
    public int getScore() {
        return (upVotes == null ? 0 : upVotes) - (downVotes == null ? 0 : downVotes);
    }
}
