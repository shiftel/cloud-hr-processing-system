package org.example.candidateservice.application.command;

import org.springframework.web.multipart.MultipartFile;

public class UploadCandidateCommand {

    private final MultipartFile file;

    public UploadCandidateCommand(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }
}
