package de.andi.minijava.assembler.exceptions;

public class StackException extends InterpreterException {

    public StackException() {
    }

    public StackException(String message) {
        super(message);
    }

    public StackException(String message, Throwable cause) {
        super(message, cause);
    }

    public StackException(Throwable cause) {
        super(cause);
    }

    public StackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}