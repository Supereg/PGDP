package codegen;

public class ArrayIndexAssignment extends Statement {

    private Expression array;
    private Expression index;
    private Expression expression;

    public ArrayIndexAssignment(Expression array, Expression index, Expression expression) {
        this.array = array;
        this.index = index;
        this.expression = expression;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getIndex() {
        return index;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}