package asm.exceptions;

public class StackOverflowException extends StackException {

    public StackOverflowException() {
        super("Stack overflowed");
    }

}