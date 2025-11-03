// package com.example.pollapp.config;

// import org.springframework.amqp.core.Binding;
// import org.springframework.amqp.core.BindingBuilder;
// import org.springframework.amqp.core.Queue;
// import org.springframework.amqp.core.TopicExchange;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class RabbitMQConfig {


//     // Whenever a new Poll is created, a topic with the same name skall be registered

//     // Once a topic has been opened, clients may subscribe on Poll updates or they may vote on some option.

    

//     public static final String QUEUE_NAME = "pollQueue";
//     public static final String EXCHANGE_NAME = "pollExchange";
//     public static final String ROUTING_KEY = "poll.#";

//     @Bean
//     public Queue queue() {
//         return new Queue(QUEUE_NAME, true);
//     }

//     @Bean
//     public TopicExchange exchange() {
//         return new TopicExchange(EXCHANGE_NAME);
//     }

//     @Bean
//     public Binding binding(Queue queue, TopicExchange exchange) {
//         return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
//     }
// }