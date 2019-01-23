package asm.exceptions;

public class NegativeMemoryAllocationException extends InterpreterException {

    public NegativeMemoryAllocationException() {
        super("Negative allocation size specified");
    }

}