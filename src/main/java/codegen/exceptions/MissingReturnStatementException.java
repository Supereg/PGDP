package codegen.exceptions;

public class MissingReturnStatementException extends CodeGenException {

    public MissingReturnStatementException(String functionName) {
        super("Function '" + functionName + "' is missing a return statement!");
    }

}