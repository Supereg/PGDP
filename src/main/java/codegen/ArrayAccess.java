package codegen;

public class ArrayAccess extends Expression {

    private Expression array;
    private Expression index;

    public ArrayAccess(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}