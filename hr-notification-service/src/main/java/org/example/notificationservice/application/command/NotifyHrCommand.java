package org.example.notificationservice.application.command;

public class NotifyHrCommand {

    private final String message;

    public NotifyHrCommand(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
