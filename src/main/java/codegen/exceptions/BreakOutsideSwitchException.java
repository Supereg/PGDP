package codegen.exceptions;

public class BreakOutsideSwitchException extends CodeGenException {

    public BreakOutsideSwitchException() {
        super("Break outside of switch statement");
    }

}