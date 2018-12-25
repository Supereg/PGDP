package de.andi.minijava.assembler.exceptions;

public class IllegalDeclarationException extends InterpreterException {

    public IllegalDeclarationException() {
        super("Illegal DECL instruction");
    }

}