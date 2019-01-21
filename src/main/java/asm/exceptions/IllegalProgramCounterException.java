package asm.exceptions;

public class IllegalProgramCounterException extends InterpreterException {

    public IllegalProgramCounterException(String message) {
        super("programm counter is in illegal state: " + message);
    }

}