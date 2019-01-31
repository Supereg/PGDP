package codegen;

import asm.Instruction;
import asm.Interpreter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CodeGenThreadsTest {

    @Test
    public void testExample() {
        Function inc = new Function(Type.Int, "inc", new Parameter[] {new Parameter(Type.IntArray, "array")}, new Declaration[0], new Statement[] {
                new Synchronized(new Variable("array"), new ArrayIndexAssignment(new Variable("array"), new Number(0),
                        new Binary(new ArrayAccess(new Variable("array"), new Number(0)), Binop.Plus, new Number(1)))),
                new Return(new Number(42))
        });
        Function main = new Function(Type.Int, "main", new Parameter[0], new Declaration[] {
                new Declaration(Type.IntArray, "array"),
                new Declaration(Type.Int, "x", "t1", "t2")
        }, new Statement[] {
                new Assignment("array", new ArrayAllocator(new Number(1))),
                new ArrayIndexAssignment(new Variable("array"), new Number(0), new Number(0)),
                new Assignment("t1", new Call(true, "inc", new Variable("array"))),
                new Assignment("t2", new Call(true, "inc", new Variable("array"))),

                new Assignment("x", new Join(new Variable("t1"))),
                new Assignment("x", new Binary(new Variable("x"), Binop.Plus, new Join(new Variable("t2")))),

                new Return(new Binary(new Variable("x"), Binop.Plus, new ArrayAccess(new Variable("array"), new Number(0))))
        });

        Program program = new Program(inc, main);

        assertEquals(86, runProgram(program));
    }

    private int runProgram(Program program) {
        CodeGenerationVisitor codeGeneration = new CodeGenerationVisitor(program);
        Instruction[] instructions = codeGeneration.getProgram();

        Interpreter interpreter = new Interpreter(instructions);
        return interpreter.execute();
    }

}