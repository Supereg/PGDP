package asm.exceptions;

public class IllegalDeclarationException extends InterpreterException {

    public IllegalDeclarationException() {
        super("Illegal DECL instruction");
    }

}