import org.junit.Assert;
import org.junit.Test;

public class FormatVisitorTest {

    @Test
    public void testExample() {
        Function main = new Function("main",
                new String[] {},
                new Declaration[] {},
                new Statement[] {
                        new ExpressionStatement(new Write(new Call("sum", new Expression[] { new Read(), new Read() }))),
                        new Return(new Number(0))
                });

        Function sum = new Function("sum",
                new String[] {"a", "b"},
                new Declaration[] {},
                new Statement[] {
                        new Return(new Binary(new Variable("a"), Binop.Plus, new Variable("b")))
                });

        Program program = new Program(new Function[] { sum, main });

        FormatVisitor fv = new FormatVisitor();
        program.accept(fv);

        Assert.assertEquals("Unexpected code format", "int sum(int a, int b) {\n" +
                "  return a + b;\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  write(sum(read(), read()));\n" +
                "  return 0;\n" +
                "}", fv.getFormattedCode());
    }

}