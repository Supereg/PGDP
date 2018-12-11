public class NotOp extends UnOp<Boolean> {

    public NotOp(Expression<Boolean> expression) {
        super(expression);
    }

    @Override
    public Boolean evaluate() {
        return !expression.evaluate();
    }

    @Override
    public String toString() {
        return "(!" + expression + ")";
    }

}