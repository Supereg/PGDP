package asm.exceptions;

public class InvalidFramePointerException extends InterpreterException {

    public InvalidFramePointerException() {
        super("Invalid frame pointer on stack; the stack has been destroyed by the program");
    }

}