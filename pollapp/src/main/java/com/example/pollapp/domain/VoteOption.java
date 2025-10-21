package com.example.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "voteOptions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String caption;
    private int presentationOrder;

    @ManyToOne
    @JsonBackReference
    private Poll poll;
}
