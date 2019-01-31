package asm.exceptions;

public class IllegalHaltInstructionException extends InterpreterException {

    public IllegalHaltInstructionException() {
        super("Illegal HALT instructions. HALT can only be executed on main thread!");
    }

}