package de.andi.minijava.codegen.exceptions;

public class FunctionNotFoundException extends CodeGenException {

    public FunctionNotFoundException(String functionName) {
        super("Could not found function: " + functionName);
    }

}