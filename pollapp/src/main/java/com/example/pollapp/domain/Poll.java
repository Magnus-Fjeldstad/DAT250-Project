package com.example.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "polls")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    @ManyToOne
    @JsonBackReference
    private User creator;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<VoteOption> options;
}
