package asm.exceptions;

public class ThreadsStillRunningException extends InterpreterException {

    public ThreadsStillRunningException() {
        super("Received HALT instruction, however there are still threads running!");
    }

}