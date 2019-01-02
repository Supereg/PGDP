package de.andi.minijava.language;

public class Switch extends Statement {

    private Expression switchExpression;

    private SwitchCase[] cases;
    private Statement defaultCase;

    public Switch(Expression switchExpression, SwitchCase[] cases, Statement defaultCase) {
        this.switchExpression = switchExpression;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }

    public Expression getSwitchExpression() {
        return switchExpression;
    }

    public SwitchCase[] getCases() {
        return cases;
    }

    public Statement getDefault() {
        return defaultCase;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}