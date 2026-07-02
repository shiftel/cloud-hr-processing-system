package org.example.verificationservice.infrastracture;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.example.verificationservice.application.command.VerifyCandidateCommand;
import org.example.verificationservice.application.command.VerifyCandidateCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MessageRelay {

    private static final Logger logger = LoggerFactory.getLogger(MessageRelay.class);
    private static final String QUEUE_FROM = "skills-extracted-queue";

    private final Channel channel;
    private final VerifyCandidateCommandHandler verifyHandler;

    public MessageRelay(Channel channel, VerifyCandidateCommandHandler verifyHandler) {
        this.channel = channel;
        this.verifyHandler = verifyHandler;
    }

    @PostConstruct
    public void startListening() throws Exception {
        channel.queueDeclare(QUEUE_FROM, true, false, false, null);
        logger.info("Verification Service: Nasłuchuję na kolejce: {}", QUEUE_FROM);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            logger.info(">>> Metoda consume() wywołana w VerificationService.");

            VerifyCandidateCommand command = new VerifyCandidateCommand(message);
            verifyHandler.handle(command);
        };

        channel.basicConsume(QUEUE_FROM, true, deliverCallback, consumerTag -> {});
    }
}