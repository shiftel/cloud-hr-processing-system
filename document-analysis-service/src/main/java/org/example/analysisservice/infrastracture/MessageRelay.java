package org.example.analysisservice.infrastracture;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.example.analysisservice.application.command.AnalyzeDocumentCommand;
import org.example.analysisservice.application.command.AnalyzeDocumentCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MessageRelay {

    private static final Logger logger = LoggerFactory.getLogger(MessageRelay.class);
    private static final String QUEUE_FROM = "cv_received_queue";

    private final Channel channel;
    private final AnalyzeDocumentCommandHandler analyzeHandler;

    public MessageRelay(Channel channel, AnalyzeDocumentCommandHandler analyzeHandler) {
        this.channel = channel;
        this.analyzeHandler = analyzeHandler;
    }

    @PostConstruct
    public void startListening() throws Exception {
        channel.queueDeclare(QUEUE_FROM, true, false, false, null);
        logger.info("Document Analysis Service: Oczekiwanie na wiadomości w kolejce: {}", QUEUE_FROM);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            logger.info(">>> Metoda consume() wywołana w Document Analysis Service.");

            AnalyzeDocumentCommand command = new AnalyzeDocumentCommand(message);
            analyzeHandler.handle(command);
        };

        channel.basicConsume(QUEUE_FROM, true, deliverCallback, consumerTag -> {});
    }
}