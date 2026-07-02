package org.example.candidateservice.infrastracture;

import org.example.candidateservice.application.command.UploadCandidateCommand;
import org.example.candidateservice.application.mediator.Mediator;
import org.example.candidateservice.application.query.DownloadCandidateQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final Mediator mediator;

    public CandidateController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        UploadCandidateCommand command = new UploadCandidateCommand(file);
        String result = mediator.send(command);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        DownloadCandidateQuery query = new DownloadCandidateQuery(id);
        return mediator.ask(query);
    }
}