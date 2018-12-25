package de.andi.minijava.assembler.exceptions;

public class StackEmptyException extends StackException {

    public StackEmptyException() {
        super("Tried accessing empty stack");
    }

    public StackEmptyException(Throwable cause) {
        super("Tried accessing empty stack", cause);
    }
}