package com.example.pollapp.repo;

import com.example.pollapp.domain.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
}
