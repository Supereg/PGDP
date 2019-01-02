package de.andi.minijava.codegen.exceptions;

public class BadArgumentSizeException extends CodeGenException {

    public BadArgumentSizeException(String functionName, int expected, int actual) {
        super("Unexpected argument size for function call '" + functionName + "'! " +
                "expected: " + expected + "; actual: " + actual);
    }

}