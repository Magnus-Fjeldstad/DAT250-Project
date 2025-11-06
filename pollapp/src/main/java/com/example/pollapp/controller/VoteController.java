package com.example.pollapp.controller;

import com.example.pollapp.dto.VoteDto;
import com.example.pollapp.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    /* Commands */

    @PostMapping("/votes")
    public ResponseEntity<?> vote(@RequestBody VoteDto cmd) {
        return voteService.vote(cmd)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /* Queries */

    @GetMapping("/votes")
    public List<VoteDto> getAllVotes() {
        return voteService.findAll();
    }

    @GetMapping("/users/{userId}/votes")
    public List<VoteDto> getVotesByUser(@PathVariable Long userId) {
        return voteService.findByUserId(userId);
    }

    @GetMapping("/polls/{pollId}/votes")
    public List<VoteDto> getVotesByPoll(@PathVariable Long pollId) {
        return voteService.findByPollId(pollId);
    }
}
