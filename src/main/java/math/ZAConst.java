package math;

public class ZAConst<T> extends ZAExpression<T> {

    private final T constant;

    public ZAConst(T constant) {
        this.constant = constant;
    }

    @Override
    public T evaluate() {
        return constant;
    }

    @Override
    public String toString() {
        return constant.toString();
    }

}