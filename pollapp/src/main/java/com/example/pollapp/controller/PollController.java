package com.example.pollapp.controller;
import com.example.pollapp.service.PollService;
import org.springframework.web.bind.annotation.RequestMapping;


import com.example.pollapp.dto.PollDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polls")
public class PollController {

    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping
    public List<PollDto> getAll() {
        return pollService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PollDto> getById(@PathVariable Long id) {
        return pollService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PollDto> create(@RequestBody PollDto dto) {
        PollDto created = pollService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PollDto> update(@PathVariable Long id, @RequestBody PollDto dto) {
        return pollService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pollService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
