package de.andi.minijava;

import de.andi.minijava.assembler.Interpreter;
import de.andi.minijava.assembler.instructions.Instruction;
import de.andi.minijava.codegen.CodeGenerationVisitor;
import de.andi.minijava.language.Number;
import de.andi.minijava.language.*;
import org.junit.Test;

import static de.andi.minijava.language.operations.Binop.Plus;
import static org.junit.Assert.assertEquals;

public class TestSwitch {

    @Test
    public void testExample() {
        int[][] expected = {
                {0, 0, 0, 0},
                {0, 1, 2, 0},
                {0, 43, 43, 43},
                {0, 1, 1, 1}
        };

        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                Program program = new Program(new Function[] {
                        new Function("main", new String[0], new Declaration[] {
                                new Declaration(new String[] {"x", "y", "z"})
                        }, new Statement[] {
                                new Assignment("x", new Number(x)),
                                new Assignment("z", new Number(0)),

                                new Switch(
                                        new Variable("x"),
                                        new SwitchCase[] {
                                                new SwitchCase(1, new Composite(new Statement[] {
                                                        new Assignment("y", new Number(y)),

                                                        new Switch(new Variable("y"), new SwitchCase[] {
                                                                new SwitchCase(1, new Composite(new Statement[] {
                                                                        new Assignment("z", new Binary(new Variable("z"), Plus, new Number(1))),
                                                                        new Break()
                                                                })),
                                                                new SwitchCase(2, new Assignment("z", new Binary(new Variable("z"), Plus, new Number(2))))
                                                        }, null),

                                                        new Break()
                                                })),
                                                new SwitchCase(2, new Assignment("z", new Number(42)))
                                        },
                                        new Assignment("z", new Binary(new Variable("z"), Plus, new Number(1)))
                                ),

                                new Return(new Variable("z"))
                        })
                });

                int result = runProgram(program);
                assertEquals("Unexpected result for x=" + x + " and y="+y, expected[x][y], result);
            }
        }
    }

    private int runProgram(Program program) {
        CodeGenerationVisitor codeGeneration = new CodeGenerationVisitor(program);
        Instruction[] instructions = codeGeneration.getProgram();

        Interpreter interpreter = new Interpreter(instructions);
        return interpreter.execute();
    }

}