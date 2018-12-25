package de.andi.minijava.assembler.exceptions;

public class IllegalProgramCounterException extends InterpreterException {

    public IllegalProgramCounterException() {
        super("programm counter is in illegal state");
    }

    public IllegalProgramCounterException(String message) {
        super("programm counter is in illegal state: " + message);
    }

    public IllegalProgramCounterException(Throwable cause) {
        super("programm counter is in illegal state", cause);
    }

}