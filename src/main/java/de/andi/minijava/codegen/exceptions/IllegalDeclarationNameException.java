package de.andi.minijava.codegen.exceptions;

public class IllegalDeclarationNameException extends CodeGenException {

    public IllegalDeclarationNameException(String declaration) {
        super("Declaration '" + declaration + "' does already exist!");
    }
}