package de.andi.minijava.assembler.exceptions;

public class IllegalVariableException extends StackException {

    public IllegalVariableException(String message) {
        super("Exception accessing element on stack: " + message);
    }

}