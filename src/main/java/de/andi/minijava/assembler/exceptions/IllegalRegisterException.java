package de.andi.minijava.assembler.exceptions;

public class IllegalRegisterException extends InterpreterException {

    public IllegalRegisterException() {
        super("Illegal register number specified");
    }

    public IllegalRegisterException(String message) {
        super("Illegal register number specified: " + message);
    }

}