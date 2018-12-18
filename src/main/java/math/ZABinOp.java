package math;

public abstract class ZABinOp<T, R> extends ZAExpression<R> {

    protected final ZAExpression<T> expression0;
    protected final ZAExpression<T> expression1;

    public ZABinOp(ZAExpression<T> expression0, ZAExpression<T> expression1) {
        this.expression0 = expression0;
        this.expression1 = expression1;
    }

}