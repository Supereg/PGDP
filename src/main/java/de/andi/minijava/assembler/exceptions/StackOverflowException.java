package de.andi.minijava.assembler.exceptions;

public class StackOverflowException extends StackException {

    public StackOverflowException() {
        super("Stack overflowed");
    }

}