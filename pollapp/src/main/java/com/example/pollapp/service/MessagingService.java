package com.example.pollapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final RabbitTemplate rabbitTemplate;
 
    public void sendPollCreated(Long pollId, String question) {
        String message = String.format("New poll created: ID=%d, Question=%s", pollId, question);
        rabbitTemplate.convertAndSend("pollapp-exchange", "pollapp-routing-key", message);
    }

    public void sendVoteCast(Long pollId, Long userId, int value) {
        String message = String.format("Vote cast: PollID=%d, UserID=%d, Value=%d", pollId, userId, value);
        rabbitTemplate.convertAndSend("pollapp-exchange", "pollapp-routing-key", message);
    }

}