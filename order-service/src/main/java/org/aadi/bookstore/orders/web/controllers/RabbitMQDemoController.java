package org.aadi.bookstore.orders.web.controllers;

import lombok.RequiredArgsConstructor;
import org.aadi.bookstore.orders.ApplicationProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RabbitMQDemoController {

    private final RabbitTemplate rabbitTemplate;
    private final ApplicationProperties applicationProperties;

    @PostMapping("/send")
    public void sendMessage(@RequestBody MyMessage myMessage) {
        rabbitTemplate.convertAndSend(
                applicationProperties.orderEventsExchange(), myMessage.routingKey(), myMessage.payload());
    }
}

record MyMessage(String routingKey, MyPayload payload) {}

record MyPayload(String content) {}
