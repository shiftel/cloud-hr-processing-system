package org.example.verificationservice.application.command;

public class VerifyCandidateCommand {

    private final String message;

    public VerifyCandidateCommand(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
