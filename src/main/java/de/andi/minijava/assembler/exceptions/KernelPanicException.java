package de.andi.minijava.assembler.exceptions;

public class KernelPanicException extends InterpreterException {

    public KernelPanicException(String message, Throwable cause) {
        super(message, cause);
    }

    public KernelPanicException(Throwable cause) {
        super(cause);
    }

}