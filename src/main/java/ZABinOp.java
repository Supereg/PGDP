public abstract class ZABinOp<T> extends ZAExpression<T> {

    protected final ZAExpression<T> expression0;
    protected final ZAExpression<T> expression1;

    public ZABinOp(ZAExpression<T> expression0, ZAExpression<T> expression1) {
        this.expression0 = expression0;
        this.expression1 = expression1;
    }

}