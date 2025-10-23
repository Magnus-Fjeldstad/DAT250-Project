package com.example.pollapp.controller;

import com.example.pollapp.security.JwtService;
import com.example.pollapp.service.UserService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService uds;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authManager, UserDetailsService uds,
                          UserService userService, JwtService jwtService) {
        this.authManager = authManager;
        this.uds = uds;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var tokenReq = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
        authManager.authenticate(tokenReq); // kaster exception ved feil

        UserDetails ud = uds.loadUserByUsername(req.getUsername());
        var domainUser = userService.findByUsername(req.getUsername()).orElseThrow();

        var roles = domainUser.getRoles(); // f.eks. List<String> ["USER"] eller ["ADMIN"]

        String jwt = jwtService.generate(
                domainUser.getUsername(),
                Map.of("roles", roles, "userId", domainUser.getId())
        );

        return ResponseEntity.ok(Map.of(
                "token", jwt,
                "userId", domainUser.getId(),
                "username", domainUser.getUsername(),
                "roles", roles
        ));
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
