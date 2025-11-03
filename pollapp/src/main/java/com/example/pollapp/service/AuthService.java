package com.example.pollapp.service;

import com.example.pollapp.domain.Role;
import com.example.pollapp.domain.User;
import com.example.pollapp.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return ((CustomUserDetails) auth.getPrincipal()).user();
    }

    public boolean isAdmin(User user) {
        return user.getRole() == Role.ROLE_ADMIN;
    }
}

