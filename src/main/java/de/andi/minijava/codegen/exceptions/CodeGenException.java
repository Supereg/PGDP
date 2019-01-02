package de.andi.minijava.codegen.exceptions;

public class CodeGenException extends RuntimeException {

    public CodeGenException() {
    }

    public CodeGenException(String message) {
        super(message);
    }

    public CodeGenException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeGenException(Throwable cause) {
        super(cause);
    }

    public CodeGenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}