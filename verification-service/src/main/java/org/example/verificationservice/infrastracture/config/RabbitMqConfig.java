package org.example.verificationservice.infrastracture.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }

    @Bean
    public Connection rabbitMqConnection(Dotenv dotenv) throws Exception {
        String rabbitMqUri = dotenv.get("RABBITMQ_URI");
        if (rabbitMqUri == null || rabbitMqUri.isEmpty()) {
            rabbitMqUri = System.getenv("RABBITMQ_URI");
        }
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(rabbitMqUri);
        Connection connection = factory.newConnection();
        logger.info("Połączono z RabbitMQ w serwisie Verification");
        return connection;
    }

    @Bean
    public Channel rabbitMqChannel(Connection rabbitMqConnection) throws Exception {
        return rabbitMqConnection.createChannel();
    }
}
