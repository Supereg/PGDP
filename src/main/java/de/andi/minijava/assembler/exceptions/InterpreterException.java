package de.andi.minijava.assembler.exceptions;

public class  InterpreterException extends RuntimeException {

    public InterpreterException() {}

    public InterpreterException(String message) {
        super(message);
    }

    public InterpreterException(Throwable cause) {
        super(cause);
    }

}