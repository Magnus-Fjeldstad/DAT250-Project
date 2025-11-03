package com.example.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "polls")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // database generates safe unique id
    private Long id;

    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    @ManyToOne
    @JsonBackReference
    private User creator;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<VoteOption> options = new ArrayList<>();
}
