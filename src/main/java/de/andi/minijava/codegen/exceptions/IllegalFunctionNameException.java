package de.andi.minijava.codegen.exceptions;

public class IllegalFunctionNameException extends CodeGenException {

    public IllegalFunctionNameException(String functionName) {
        this(functionName, "");
    }

    public IllegalFunctionNameException(String functionName, String message) {
        super("Illegal function name '" + functionName + "': " + message);
    }
}