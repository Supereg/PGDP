package asm.exceptions;

public class LocksStillAcquiredException extends InterpreterException {

    public LocksStillAcquiredException() {
        super("Received HALT instructions, however there are still locks acquired");
    }

}