package utils.math;

public abstract class BinOp<T> extends Expression<T> {

    protected final Expression<T> expression0;
    protected final Expression<T> expression1;

    public BinOp(Expression<T> expression0, Expression<T> expression1) {
        this.expression0 = expression0;
        this.expression1 = expression1;
    }

}