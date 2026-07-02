package org.example.skillextractor.application.command;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExtractSkillsCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExtractSkillsCommandHandler.class);

    private final Channel channel;

    public ExtractSkillsCommandHandler(Channel channel) {
        this.channel = channel;
    }

    public void handle(ExtractSkillsCommand command) {
        String message = command.getMessage();

        logger.info("Rozpoczynam analizę struktury dla: {}", message);
        logger.info("Analiza zakończona pomyślnie.");

        String queueTo = "skills-extracted-queue";
        String nextStepMsg = message + " | ANALYZED";

        try {
            channel.queueDeclare(queueTo, true, false, false, null);
            channel.basicPublish("", queueTo, null, nextStepMsg.getBytes());
            logger.info("Przekazano informację do kolejki: {}", queueTo);
        } catch (Exception e) {
            logger.error("Błąd podczas publikacji do kolejki", e);
            throw new RuntimeException("Błąd przekazywania wiadomości", e);
        }
    }
}
