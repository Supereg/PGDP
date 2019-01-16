package utils.math;

public class SubOp<X> extends BinOp<Integer> {

    public SubOp(Expression<Integer> expression0, Expression<Integer> expression1) {
        super(expression0, expression1);
    }

    @Override
    public Integer evaluate() {
        return expression0.evaluate() - expression1.evaluate();
    }

    @Override
    public String toString() {
        return "(" + expression0 + " - " + expression1 + ")";
    }

}