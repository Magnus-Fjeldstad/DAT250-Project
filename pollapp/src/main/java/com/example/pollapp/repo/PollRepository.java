package com.example.pollapp.repo;

import com.example.pollapp.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Long> {
}
