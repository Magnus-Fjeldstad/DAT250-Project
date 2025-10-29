package com.example.pollapp.controller;

import com.example.pollapp.domain.Role;
import com.example.pollapp.domain.User;
import com.example.pollapp.dto.LoginRequestDto;
import com.example.pollapp.dto.RegisterRequestDto;
import com.example.pollapp.mapper.UserMapper;
import com.example.pollapp.security.CustomUserDetails;
import com.example.pollapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    // Register new user using DTO, not the entity
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequestDto request) {
        User user = userMapper.fromRegisterDto(request);
        User saved = userService.create(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequestDto request,
                                      HttpServletRequest httpRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            httpRequest.getSession(true);

            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            User loggedInUser = principal.user(); // extract domain user
            loggedInUser.setPassword(null);

            return ResponseEntity.ok(loggedInUser);
        }

        return ResponseEntity.status(401).build();
    }

    @GetMapping("/me")
    public ResponseEntity<User> me() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = principal.user();
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}
