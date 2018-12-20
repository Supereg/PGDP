public class Return extends Statement {

    private Expression expression; // TODO not null

    public Return(Expression expression) { // TODO make this subclass of ExpressionStatement
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}