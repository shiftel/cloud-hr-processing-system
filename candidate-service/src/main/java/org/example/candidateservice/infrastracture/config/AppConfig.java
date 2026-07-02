package org.example.candidateservice.infrastracture.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }

    @Bean
    public S3Client s3Client(Dotenv dotenv) {
        String accessKey = dotenv.get("AWS_ACCESS_KEY");
        if (accessKey == null || accessKey.isEmpty()) {
            accessKey = System.getenv("AWS_ACCESS_KEY");
        }
        String secretKey = dotenv.get("AWS_SECRET_KEY");
        if (secretKey == null || secretKey.isEmpty()) {
            secretKey = System.getenv("AWS_SECRET_KEY");
        }

        S3Client client = S3Client.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
        logger.info("Połączono z AWS S3");
        return client;
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
        logger.info("Połączono z RabbitMQ w serwisie Candidate");
        return connection;
    }
}
