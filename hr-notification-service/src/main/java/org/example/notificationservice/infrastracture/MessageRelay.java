package org.example.notificationservice.infrastracture;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.example.notificationservice.application.command.NotifyHrCommand;
import org.example.notificationservice.application.command.NotifyHrCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MessageRelay {

    private static final Logger logger = LoggerFactory.getLogger(MessageRelay.class);
    private static final String QUEUE_FROM = "verification-passed-queue";

    private final Channel channel;
    private final NotifyHrCommandHandler notifyHandler;

    public MessageRelay(Channel channel, NotifyHrCommandHandler notifyHandler) {
        this.channel = channel;
        this.notifyHandler = notifyHandler;
    }

    @PostConstruct
    public void startListening() throws Exception {
        channel.queueDeclare(QUEUE_FROM, true, false, false, null);
        logger.info("Notification Service: Nasłuchuję na {}", QUEUE_FROM);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            logger.info(">>> Metoda consume() wywołana - FINAL STEP.");

            NotifyHrCommand command = new NotifyHrCommand(message);
            notifyHandler.handle(command);
        };

        channel.basicConsume(QUEUE_FROM, true, deliverCallback, consumerTag -> {});
    }
}