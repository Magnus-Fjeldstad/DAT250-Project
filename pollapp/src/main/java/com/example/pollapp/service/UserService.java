package com.example.pollapp.service;

import com.example.pollapp.domain.User;
import com.example.pollapp.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepository;

    public UserService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public Optional<User> update(Long id, User updated) {
        return userRepository.findById(id).map(existing -> {
            existing.setUsername(updated.getUsername());
            existing.setEmail(updated.getEmail());
            return userRepository.save(existing);
        });
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
