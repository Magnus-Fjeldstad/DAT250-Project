package com.example.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    @JsonIgnore
    private String password;
    private Role role;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Poll> createdPolls;

    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Vote> votes;
}
