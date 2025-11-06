package com.example.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "votes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // database generates safe unique id
    private Long id;

    private Instant publishedAt;

    /**
     * +1 = oppvote, -1 = nedvote
     */
    @Column(name = "vote_value")
    private int value;

    @ManyToOne
    @JsonBackReference
    private User voter;

    @ManyToOne
    private VoteOption votedOn;
}
