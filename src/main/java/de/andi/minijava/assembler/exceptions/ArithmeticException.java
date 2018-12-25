package de.andi.minijava.assembler.exceptions;

public class ArithmeticException extends InterpreterException {

    public ArithmeticException(String message) {
        super(message);
    }

    public ArithmeticException(String message, Throwable cause) {
        super(message, cause);
    }

}