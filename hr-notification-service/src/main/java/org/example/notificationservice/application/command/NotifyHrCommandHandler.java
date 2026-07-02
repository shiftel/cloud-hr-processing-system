package org.example.notificationservice.application.command;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotifyHrCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotifyHrCommandHandler.class);

    private final Channel channel;

    public NotifyHrCommandHandler(Channel channel) {
        this.channel = channel;
    }

    public void handle(NotifyHrCommand command) {
        String message = command.getMessage();

        logger.info("POWIADOMIENIE HR: Kandydat [{}] przetworzony.", message);

        String queueTo = "final-results-storage-queue";
        String finalLog = "PROCES ZAKOŃCZONY SUKCESEM: " + message;

        try {
            channel.queueDeclare(queueTo, true, false, false, null);
            channel.basicPublish("", queueTo, null, finalLog.getBytes());
            logger.info("Wiadomość zarchiwizowana w kolejce: {}", queueTo);
        } catch (Exception e) {
            logger.error("Błąd podczas publikacji do kolejki", e);
            throw new RuntimeException("Błąd przekazywania wiadomości", e);
        }
    }
}
