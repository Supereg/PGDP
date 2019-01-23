package asm.exceptions;

public class OutOfMemoryException extends InterpreterException {

    public OutOfMemoryException() {
        super("Out of memory: heap is full");
    }

}