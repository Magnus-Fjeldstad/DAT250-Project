package com.example.pollapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PollMessageListener {

    @RabbitListener(queues = "pollapp-queue")
    public void handleMessage(String message) {
        log.info("Received message from RabbitMQ: {}", message);
    }
}