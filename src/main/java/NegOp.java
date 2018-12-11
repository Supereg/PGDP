public class NegOp extends UnOp<Integer> {

    public NegOp(Expression<Integer> expression) {
        super(expression);
    }

    @Override
    public Integer evaluate() {
        return - expression.evaluate();
    }

    @Override
    public String toString() {
        return "(-" + expression + ")";
    }

}