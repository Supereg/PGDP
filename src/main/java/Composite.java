public class Composite extends Statement {

    private Statement[] statements;

    public Composite(Statement[] statements) { // TODO not null
        this.statements = statements;
    }

    public Statement[] getStatements() {
        return statements;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}