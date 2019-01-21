package asm.exceptions;

public class StackUnderflowException extends StackException {

    public StackUnderflowException() {
        super("Tried accessing empty stack");
    }

}