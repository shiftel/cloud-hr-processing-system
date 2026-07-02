package org.example.skillextractor.infrastracture;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.example.skillextractor.application.command.ExtractSkillsCommand;
import org.example.skillextractor.application.command.ExtractSkillsCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MessageRelay {

    private static final Logger logger = LoggerFactory.getLogger(MessageRelay.class);
    private static final String QUEUE_FROM = "analysis-done-queue";

    private final Channel channel;
    private final ExtractSkillsCommandHandler extractHandler;

    public MessageRelay(Channel channel, ExtractSkillsCommandHandler extractHandler) {
        this.channel = channel;
        this.extractHandler = extractHandler;
    }

    @PostConstruct
    public void startListening() throws Exception {
        channel.queueDeclare(QUEUE_FROM, true, false, false, null);
        logger.info("Skills Extractor Service: Oczekiwanie na wiadomości w kolejce: {}", QUEUE_FROM);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            logger.info(">>> Metoda consume() wywołana w Skill Extractor Service.");

            ExtractSkillsCommand command = new ExtractSkillsCommand(message);
            extractHandler.handle(command);
        };

        channel.basicConsume(QUEUE_FROM, true, deliverCallback, consumerTag -> {});
    }
}