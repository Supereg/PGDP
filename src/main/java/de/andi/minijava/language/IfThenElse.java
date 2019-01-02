package de.andi.minijava.language;

public class IfThenElse extends IfThen {

    private Statement elseBranch;

    public IfThenElse(Condition condition, Statement thenBranch, Statement elseBranch) {
        super(condition, thenBranch);
        this.elseBranch = elseBranch;
    }

    public Statement getElseBranch() {
        return elseBranch;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}