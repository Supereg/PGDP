public class ZAEQOp<X, Y> extends ZABinOp<Integer, Boolean> {

    public ZAEQOp(ZAExpression<Integer> expression0, ZAExpression<Integer> expression1) {
        super(expression0, expression1);
    }

    @Override
    public Boolean evaluate() {
        return expression0.evaluate().equals(expression1.evaluate());
    }

    @Override
    public String toString() {
        return "(" + expression0 + " == " + expression1 + ")";
    }

}