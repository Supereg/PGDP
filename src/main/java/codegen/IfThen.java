package codegen;

public class IfThen extends Statement {

    private Condition condition;

    private Statement thenBranch;

    public IfThen(Condition condition, Statement thenBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
    }

    public Condition getCond() {
        return condition;
    }

    public Statement getThenBranch() {
        return thenBranch;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}