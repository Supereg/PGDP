package asm.exceptions;

public class DeadLockException extends InterpreterException {

    public DeadLockException() {
        super("Encountered dead lock");
    }

}