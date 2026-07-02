package org.example.skillextractor.application.command;

public class ExtractSkillsCommand {

    private final String message;

    public ExtractSkillsCommand(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
