package com.example.pollapp.repo;

import com.example.pollapp.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByVoterIdAndVotedOnId(Long userId, Long optionId);

    List<Vote> findAllByVoterId(Long userId);

    List<Vote> findAllByVotedOn_Poll_Id(Long pollId);
}
