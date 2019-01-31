package codegen;

public class Call extends Expression {

    private boolean fork;
    private String functionName;
    private Expression[] arguments;

    public Call(boolean fork, String functionName, Expression... arguments) {
        this.fork = fork;
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public Call(String functionName, Expression[] arguments, boolean fork) { // just to be save
        this(fork, functionName, arguments);
    }

    public Call(String functionName, Expression... arguments) {
        this(false, functionName, arguments);
    }

    public boolean isFork() {
        return fork;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Expression[] getArguments() {
        return arguments;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}