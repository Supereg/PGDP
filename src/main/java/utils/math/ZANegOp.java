package utils.math;

public class ZANegOp<T> extends ZAUnOp<T> {

    public ZANegOp(ZAExpression<T> expression) {
        super(expression);
    }

    @SuppressWarnings({"Duplicates", "unchecked"})
    @Override
    public T evaluate() {
        T t = expression.evaluate();

        if (t instanceof Boolean) {
            Boolean b0 = (Boolean) t;
            b0 = !b0;
            return (T) b0;
        }
        else if (t instanceof Integer) {
            Integer n0 = (Integer) t;
            n0 = -n0;
            return (T) n0;
        }
        else
            throw new IllegalArgumentException("Unsupported type to negate");
    }

    @SuppressWarnings("Duplicates")
    @Override
    public String toString() {
        T t = evaluate();
        // we need to evaluate here what type T is since math.ZANegOp<Integer> and math.ZANegOp<Boolean> are the same at runtime

        if (t instanceof Boolean)
            return "(!" + expression + ")";
        else if (t instanceof Number)
            return "(-" + expression + ")";
        else
            throw new IllegalArgumentException("Unsupported type to negate");
    }

}