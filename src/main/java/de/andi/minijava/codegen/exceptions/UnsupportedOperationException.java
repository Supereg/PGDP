package de.andi.minijava.codegen.exceptions;

public class UnsupportedOperationException extends CodeGenException {

    public UnsupportedOperationException() {
        this("");
    }

    public UnsupportedOperationException(String message) {
        super("Unsupported operation: " + message);
    }

}