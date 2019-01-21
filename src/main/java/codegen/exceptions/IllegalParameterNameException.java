package codegen.exceptions;

public class IllegalParameterNameException extends CodeGenException {

    public IllegalParameterNameException(String parameter) {
        super("Parameter '" + parameter + "' does already exist in scope!");
    }
}