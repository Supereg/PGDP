import de.andi.minijava.assembler.AsmFormatVisitor;
import de.andi.minijava.assembler.Interpreter;
import de.andi.minijava.assembler.instructions.Instruction;
import de.andi.minijava.codegen.CodeGenerationVisitor;
import de.andi.minijava.language.Number;
import de.andi.minijava.language.*;
import org.junit.Assert;
import org.junit.Test;

public class CodegenTest {

    @Test
    public void testFak() {
        int[] expected = {
                1,
                1,
                2,
                6,
                24,
                120
        };

        for (int i = 1; i < 5; i++) {
            Program program = new Program(
                    new Function[] {
                            new Function("fak", new String[] {"n"}, new Declaration[0], new Statement[] {
                                    new IfThen(
                                            new Comparison(new Variable("n"), de.andi.minijava.language.operations.Comp.Equals, new de.andi.minijava.language.Number(1)),
                                            new Return(new de.andi.minijava.language.Number(1))
                                    ),
                                    new Return(new Binary(new Variable("n"), de.andi.minijava.language.operations.Binop.MultiplicationOperator,
                                            new Call("fak", new Expression[]{new Binary(new Variable("n"), de.andi.minijava.language.operations.Binop.Minus, new de.andi.minijava.language.Number(1))})))
                            }),
                            new Function("main", new String[0], new Declaration[0], new Statement[] {
                                    new Return(new Call("fak", new Expression[] {new Number(i)}))
                            })
                    }
            );

            int result = runProgram(program);
            Assert.assertEquals("Unexpected result for fak(" + i + ")", expected[i], result);
        }
    }

    private int runProgram(Program program) {
        CodeGenerationVisitor codeGeneration = new CodeGenerationVisitor(program);
        Instruction[] instructions = codeGeneration.getProgram();

        AsmFormatVisitor format = new AsmFormatVisitor(instructions);
        System.out.println(format.getFormattedCode());// TODO debug

        Interpreter interpreter = new Interpreter(instructions);
        return interpreter.execute();
    }

}