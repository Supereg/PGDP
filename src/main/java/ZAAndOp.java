public class ZAAndOp extends ZABinOp<Boolean> {

    public ZAAndOp(ZAExpression<Boolean> expression0, ZAExpression<Boolean> expression1) {
        super(expression0, expression1);
    }

    @Override
    public Boolean evaluate() {
        return expression0.evaluate() && expression1.evaluate();
    }

    @Override
    public String toString() {
        return "(" + expression0 + " & " + expression1 + ")";
    }

}