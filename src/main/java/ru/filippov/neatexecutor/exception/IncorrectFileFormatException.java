package ru.filippov.neatexecutor.exception;

public class IncorrectFileFormatException extends Exception {
    public IncorrectFileFormatException() {
        super();
    }

    public IncorrectFileFormatException(String message) {
        super(message);
    }

    public IncorrectFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectFileFormatException(Throwable cause) {
        super(cause);
    }
}
