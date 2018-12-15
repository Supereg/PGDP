public class NegOp<T> extends UnOp<T> {

    public NegOp(Expression<T> expression) {
        super(expression);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public T evaluate() {
        T t = expression.evaluate();

        if (t instanceof Boolean) {
            Boolean b0 = (Boolean) t;
            b0 = !b0;
            //noinspection unchecked
            return (T) b0;
        }
        else if (t instanceof Integer) {
            Integer n0 = (Integer) t;
            n0 = -n0;
            //noinspection unchecked
            return (T) n0;
        }
        else
            throw new IllegalArgumentException("Unsupported type to negate");
    }

    @SuppressWarnings("Duplicates")
    @Override
    public String toString() {
        T t = evaluate();
        // we need to evaluate here what type T is since ZANegOp<Integer> and ZANegOp<Boolean> are the same at runtime

        if (t instanceof Boolean)
            return "(!" + expression + ")";
        else if (t instanceof Number)
            return "(-" + expression + ")";
        else
            throw new IllegalArgumentException("Unsupported type to negate");
    }

}