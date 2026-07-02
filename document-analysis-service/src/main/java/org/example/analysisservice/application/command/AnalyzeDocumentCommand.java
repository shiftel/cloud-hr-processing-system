package org.example.analysisservice.application.command;

public class AnalyzeDocumentCommand {

    private final String message;

    public AnalyzeDocumentCommand(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
