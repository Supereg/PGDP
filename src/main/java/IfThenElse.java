public class IfThenElse extends Statement {

    private Condition condition; // TODO shouldn't be null

    private Statement thenBranch;
    private Statement elseBranch;

    public IfThenElse(Condition condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Condition getCond() {
        return condition;
    }

    public Statement getThenBranch() {
        return thenBranch;
    }

    public Statement getElseBranch() {
        return elseBranch;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}