// src/main/java/com/example/pollapp/config/DataInitializer.java
package com.example.pollapp.config;

import com.example.pollapp.domain.Role;
import com.example.pollapp.domain.User;
import com.example.pollapp.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Insert a demo user at application startup when running in the 'dev' profile.
 * Uses an available PasswordEncoder bean if present, otherwise falls back to BCryptPasswordEncoder.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepo userRepository, ObjectProvider<PasswordEncoder> passwordEncoderProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoderProvider.getIfAvailable(BCryptPasswordEncoder::new);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String username = "test";
        if (userRepository.findByUsername(username).isEmpty()) {
            User u = new User();
            u.setUsername(username);
            u.setEmail("test@example.com");
            // plain text password below -> encoded before saving
            u.setPassword(passwordEncoder.encode("test"));
            // ensure your Role enum contains ROLE_USER (or adapt accordingly)
            u.setRole(Role.ROLE_ADMIN);
            userRepository.save(u);
            System.out.println("Inserted demo user 'test' (password: password)");
        } else {
            System.out.println("Demo user already exists");
        }
    }
}
