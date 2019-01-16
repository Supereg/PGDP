package utils.math;

public class AndOp<X> extends BinOp<Boolean> {

    public AndOp(Expression<Boolean> expression0, Expression<Boolean> expression1) {
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