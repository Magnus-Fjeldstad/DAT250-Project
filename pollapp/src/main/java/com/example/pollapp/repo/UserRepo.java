package com.example.pollapp.repo;

import com.example.pollapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
