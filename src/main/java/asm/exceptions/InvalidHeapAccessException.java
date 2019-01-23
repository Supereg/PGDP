package asm.exceptions;

public class InvalidHeapAccessException extends InterpreterException {

    public InvalidHeapAccessException() {
        super("Heap address is out of range");
    }

}