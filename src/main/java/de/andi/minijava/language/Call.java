package de.andi.minijava.language;

public class Call extends Expression {

    private String functionName;
    private Expression[] arguments;

    public Call(String functionName, Expression[] arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
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