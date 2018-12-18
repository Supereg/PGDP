package math;

public class ZAMulOp<X, Y> extends ZABinOp<Integer, Integer> {

    public ZAMulOp(ZAExpression<Integer> expression0, ZAExpression<Integer> expression1) {
        super(expression0, expression1);
    }

    @Override
    public Integer evaluate() {
        return expression0.evaluate() * expression1.evaluate();
    }

    @Override
    public String toString() {
        return "(" + expression0 + " * " + expression1 + ")";
    }

}