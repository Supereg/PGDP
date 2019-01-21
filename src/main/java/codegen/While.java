package codegen;

public class While extends Statement {

    private Condition condition;
    private Statement body;

    private boolean doWhile;

    public While(Condition condition, Statement body, boolean doWhile) {
        this.condition = condition;
        this.body = body;
        this.doWhile = doWhile;
    }

    public Condition getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }

    public boolean isDoWhile() {
        return doWhile;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}