package de.andi.minijava.assembler.exceptions;

public class StackEmptyException extends StackException {

    public StackEmptyException() {
        super("Tried accessing empty stack");
    }

}