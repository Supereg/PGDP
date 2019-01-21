package codegen.exceptions;

public class VariableNotInitiliazedException extends CodeGenException {

    public VariableNotInitiliazedException(String name) {
        super("The following variable wasn't initialized: " + name);
    }

}