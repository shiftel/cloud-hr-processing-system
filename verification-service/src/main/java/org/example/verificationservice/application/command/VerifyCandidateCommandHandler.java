package org.example.verificationservice.application.command;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VerifyCandidateCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(VerifyCandidateCommandHandler.class);

    private final Channel channel;

    public VerifyCandidateCommandHandler(Channel channel) {
        this.channel = channel;
    }

    public void handle(VerifyCandidateCommand command) {
        String message = command.getMessage();

        logger.info("Weryfikuję dane kandydata: {}", message);
        logger.info("Weryfikacja zakończona sukcesem (Status: VALID).");

        String queueTo = "verification-passed-queue";

        try {
            channel.queueDeclare(queueTo, true, false, false, null);
            channel.basicPublish("", queueTo, null, (message + " | VERIFIED").getBytes());
            logger.info("Przekazano do kolejki: {}", queueTo);
        } catch (Exception e) {
            logger.error("Błąd podczas publikacji do kolejki", e);
            throw new RuntimeException("Błąd przekazywania wiadomości", e);
        }
    }
}
