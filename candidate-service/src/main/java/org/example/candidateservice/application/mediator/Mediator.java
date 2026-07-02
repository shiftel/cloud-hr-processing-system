package org.example.candidateservice.application.mediator;

import org.example.candidateservice.application.command.UploadCandidateCommand;
import org.example.candidateservice.application.command.UploadCandidateCommandHandler;
import org.example.candidateservice.application.query.DownloadCandidateQuery;
import org.example.candidateservice.application.query.DownloadCandidateQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class Mediator {

    private final UploadCandidateCommandHandler uploadHandler;
    private final DownloadCandidateQueryHandler downloadHandler;

    public Mediator(UploadCandidateCommandHandler uploadHandler,
                    DownloadCandidateQueryHandler downloadHandler) {
        this.uploadHandler = uploadHandler;
        this.downloadHandler = downloadHandler;
    }

    public String send(UploadCandidateCommand command) {
        return uploadHandler.handle(command);
    }

    public ResponseEntity<byte[]> ask(DownloadCandidateQuery query) {
        return downloadHandler.handle(query);
    }
}
