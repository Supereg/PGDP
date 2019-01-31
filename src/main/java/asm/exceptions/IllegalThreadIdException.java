package asm.exceptions;

public class IllegalThreadIdException extends InterpreterException {

    public IllegalThreadIdException() {
        super("Tried joining an non existing thread id");
    }

}