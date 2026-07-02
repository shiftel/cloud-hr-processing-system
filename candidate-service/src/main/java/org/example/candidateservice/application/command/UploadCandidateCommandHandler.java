package org.example.candidateservice.application.command;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.candidateservice.domain.CandidateApplication;
import org.example.candidateservice.infrastracture.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class UploadCandidateCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(UploadCandidateCommandHandler.class);

    private final CandidateRepository repository;
    private final S3Client s3Client;
    private final Connection rabbitMqConnection;
    private final String bucketName;

    public UploadCandidateCommandHandler(CandidateRepository repository,
                                         S3Client s3Client,
                                         Connection rabbitMqConnection,
                                         Dotenv dotenv) {
        this.repository = repository;
        this.s3Client = s3Client;
        this.rabbitMqConnection = rabbitMqConnection;
        String bucket = dotenv.get("S3_BUCKET_NAME");
        if (bucket == null || bucket.isEmpty()) {
            bucket = System.getenv("S3_BUCKET_NAME");
        }
        this.bucketName = bucket;
    }

    public String handle(UploadCandidateCommand command) {
        MultipartFile file = command.getFile();
        String name = file.getOriginalFilename();
        String ext = name != null && name.contains(".") ? name.substring(name.lastIndexOf(".")) : "unknown";
        String s3Key = "cv/" + UUID.randomUUID() + "_" + name;

        logger.info("Odebrano plik: {} | Rozszerzenie: {}", name, ext);

        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            logger.info("Plik wgrany do S3: {}", s3Key);

            CandidateApplication app = new CandidateApplication(name, ext, file.getSize(), s3Key);
            repository.save(app);
            logger.info("Zapisano dane w candidate_db. ID aplikacji: {}", app.getId());

            try (Channel channel = rabbitMqConnection.createChannel()) {
                channel.queueDeclare("cv_received_queue", true, false, false, null);
                String message = "PROCESS_CV:" + app.getId();
                channel.basicPublish("", "cv_received_queue", null, message.getBytes());
                logger.info("Wysłano zadanie do brokera: {}", message);
            }

            return "Plik przyjęty do rekrutacji!";

        } catch (Exception e) {
            logger.error("Błąd podczas przetwarzania uploadu", e);
            throw new RuntimeException("Błąd serwera", e);
        }
    }
}
