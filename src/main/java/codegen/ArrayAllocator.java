package codegen;

public class ArrayAllocator extends Expression {

    private Expression size;

    public ArrayAllocator(Expression size) {
        this.size = size;
    }

    public Expression getSize() {
        return size;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}