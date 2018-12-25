package de.andi.minijava.language;

public class IfThen extends Statement { // TODO make this super class of IfThenElse

    private Condition condition; // TODO shouldn bet null

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