package com.example.pollapp.mapper;

import com.example.pollapp.domain.Role;
import com.example.pollapp.domain.User;
import com.example.pollapp.dto.RegisterRequestDto;
import org.springframework.stereotype.Component;

// Maps incoming DTOs to domain entities
@Component
public class UserMapper {

    public User fromRegisterDto(RegisterRequestDto dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(Role.ROLE_USER);
        return user;
    }
}
