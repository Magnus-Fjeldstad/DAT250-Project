package com.example.pollapp.security;

import com.example.pollapp.service.PollService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("pollSecurity")
public class PollSecurity {

    private final PollService pollService;

    public PollSecurity(PollService pollService) {
        this.pollService = pollService;
    }

    public boolean isOwner(Long pollId, Authentication auth) {
        if (auth == null || auth.getName() == null) return false;
        return pollService.isOwnerUsername(pollId, auth.getName());
    }
}

