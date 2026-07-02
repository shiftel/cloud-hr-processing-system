package org.example.candidateservice.application.query;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.candidateservice.domain.CandidateApplication;
import org.example.candidateservice.infrastracture.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class DownloadCandidateQueryHandler {

    private static final Logger logger = LoggerFactory.getLogger(DownloadCandidateQueryHandler.class);

    private final CandidateRepository repository;
    private final S3Client s3Client;
    private final String bucketName;

    public DownloadCandidateQueryHandler(CandidateRepository repository,
                                         S3Client s3Client,
                                         Dotenv dotenv) {
        this.repository = repository;
        this.s3Client = s3Client;
        String bucket = dotenv.get("S3_BUCKET_NAME");
        if (bucket == null || bucket.isEmpty()) {
            bucket = System.getenv("S3_BUCKET_NAME");
        }
        this.bucketName = bucket;
    }

    public ResponseEntity<byte[]> handle(DownloadCandidateQuery query) {
        CandidateApplication app = repository.findById(query.getId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono aplikacji o ID: " + query.getId()));

        String s3Key = app.getS3path();

        try {
            ResponseBytes<GetObjectResponse> s3Object = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build());

            byte[] content = s3Object.asByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + app.getFileName() + "\"")
                    .body(content);

        } catch (Exception e) {
            logger.error("Błąd podczas pobierania pliku z S3", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
