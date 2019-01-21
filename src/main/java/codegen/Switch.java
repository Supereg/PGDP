package codegen;

public class Switch extends Statement {

    private SwitchCase[] cases;
    private Statement defaultCase;

    private Expression switchExpression;

    public Switch(SwitchCase[] cases, Statement defaultCase, Expression switchExpression) {
        this.cases = cases;
        this.defaultCase = defaultCase;
        this.switchExpression = switchExpression;
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