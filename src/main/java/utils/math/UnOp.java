package utils.math;

public abstract class UnOp<T> extends Expression<T> {

    protected final Expression<T> expression;

    public UnOp(Expression<T> expression) {
        this.expression = expression;
    }

}