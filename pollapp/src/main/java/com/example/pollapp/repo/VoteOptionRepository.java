package com.example.pollapp.repo;

import com.example.pollapp.domain.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    List<VoteOption> findByPollIdOrderByPresentationOrderAsc(Long pollId);
}
