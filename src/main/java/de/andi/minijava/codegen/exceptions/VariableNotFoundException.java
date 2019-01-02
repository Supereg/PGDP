package de.andi.minijava.codegen.exceptions;

public class VariableNotFoundException extends CodeGenException {

    public VariableNotFoundException(String variable) {
        super("Could not found variable: " + variable);
    }

}