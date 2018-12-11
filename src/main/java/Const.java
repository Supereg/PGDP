public class Const<T> extends Expression<T> {

    private final T constant;

    public Const(T constant) {
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