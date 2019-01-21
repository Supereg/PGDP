package codegen.exceptions;

public class UnsupportedOperationException extends CodeGenException {

    public UnsupportedOperationException(String message) {
        super("Unsupported operation: " + message);
    }

}