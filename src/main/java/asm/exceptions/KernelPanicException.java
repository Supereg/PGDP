package asm.exceptions;

public class KernelPanicException extends InterpreterException {

    public KernelPanicException(Throwable cause) {
        super(cause);
    }

}