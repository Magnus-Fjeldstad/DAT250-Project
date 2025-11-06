package com.example.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "voteOptions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // database generates safe unique id
    private Long id;

    private String caption;
    private int presentationOrder;

    @ManyToOne
    @JsonBackReference
    private Poll poll;


    @OneToMany(mappedBy = "votedOn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
}
