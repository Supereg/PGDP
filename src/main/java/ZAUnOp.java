public abstract class ZAUnOp<T> extends ZAExpression<T> {

    protected final ZAExpression<T> expression;

    public ZAUnOp(ZAExpression<T> expression) {
        this.expression = expression;
    }

}