package de.andi.minijava;

import de.andi.minijava.language.Number;
import de.andi.minijava.language.*;
import de.andi.minijava.language.operations.*;
import org.junit.Test;

import static de.andi.minijava.language.operations.Binop.*;
import static org.junit.Assert.assertEquals;

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

        assertEquals("Unexpected code format", "int sum(int a, int b) {\n" +
                "  return a + b;\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  write(sum(read(), read()));\n" +
                "  return 0;\n" +
                "}", fv.getFormattedCode());
    }

    @Test
    public void testFak() {
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
                                new ExpressionStatement(new Write(new Call("fak", new Expression[] {new Read()}))),
                                new Return(new Number(0))
                        })
                }
        );

        FormatVisitor visitor = new FormatVisitor();
        program.accept(visitor);

        assertEquals("Unexpected code format", "int fak(int n) {\n" +
                "  if (n == 1)\n" +
                "    return 1;\n" +
                "  return n * fak(n - 1);\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  write(fak(read()));\n" +
                "  return 0;\n" +
                "}", visitor.getFormattedCode());
    }

    @Test
    public void testGGT() {
        Program program = new Program(new Function[] {
                new Function("ggt", new String[] {"a", "b"}, new Declaration[] {new Declaration(new String[] {"temp"})}, new Statement[] {
                        new IfThen(new Comparison(new Variable("b"), Comp.GreaterEqual, new Variable("a")), new Composite(new Statement[] {
                                new Assignment("temp", new Variable("b")),
                                new Assignment("b", new Variable("a")),
                                new Assignment("a", new Variable("temp"))
                        })),
                        new While(new Comparison(new Variable("b"), Comp.NotEquals, new Number(0)), new Composite(new Statement[] {
                                new Assignment("temp", new Variable("b")),
                                new Assignment("b", new Binary(new Variable("a"), Modulo, new Variable("b"))),
                                new Assignment("a", new Variable("temp"))
                        }), true),
                        new Return(new Variable("a"))
                }),
                new Function("main", new String[0], new Declaration[0], new Statement[] {
                        new ExpressionStatement(new Write(new Call("ggt", new Expression[] {
                                new Read(),
                                new Read()
                        }))),
                        new Return(new Number(0))
                })
        });

        FormatVisitor visitor = new FormatVisitor();
        program.accept(visitor);

        assertEquals("Unexpected code format", "int ggt(int a, int b) {\n" +
                "  int temp;\n" +
                "  if (b >= a) {\n" +
                "    temp = b;\n" +
                "    b = a;\n" +
                "    a = temp;\n" +
                "  }\n" +
                "  do {\n" +
                "    temp = b;\n" +
                "    b = a % b;\n" +
                "    a = temp;\n" +
                "  } while (b != 0);\n" +
                "  return a;\n" +
                "}\n" +
                "\n" +
                "int main() {\n" +
                "  write(ggt(read(), read()));\n" +
                "  return 0;\n" +
                "}", visitor.getFormattedCode());
    }

    @Test
    public void testSwitchExample() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[] {
                        new Declaration(new String[] {"x", "y", "z"})
                }, new Statement[] {
                        new Assignment("x", new Read()),
                        new Assignment("z", new Number(0)),

                        new Switch(
                                new Variable("x"),
                                new SwitchCase[] {
                                        new SwitchCase(1, new Composite(new Statement[] {
                                                new Assignment("y", new Read()),

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

                        new ExpressionStatement(new Write(new Variable("z"))),
                        new Return(new Number(0))
                })
        });

        FormatVisitor visitor = new FormatVisitor();
        program.accept(visitor);

        assertEquals("Unexpected code format", "int main() {\n" +
                "  int x, y, z;\n" +
                "  x = read();\n" +
                "  z = 0;\n" +
                "  switch (x) {\n" +
                "    case 1: {\n" +
                "      y = read();\n" +
                "      switch (y) {\n" +
                "        case 1: {\n" +
                "          z = z + 1;\n" +
                "          break;\n" +
                "        }\n" +
                "        case 2:\n" +
                "          z = z + 2;\n" +
                "      }\n" +
                "      break;\n" +
                "    }\n" +
                "    case 2:\n" +
                "      z = 42;\n" +
                "    default:\n" +
                "      z = z + 1;\n" +
                "  }\n" +
                "  write(z);\n" +
                "  return 0;\n" +
                "}", visitor.getFormattedCode());
    }

    @Test
    public void formatTest() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[0], new Statement[] {
                        new Return(new Call("test", new Expression[] {
                                new Number(1),
                                new Number(2)
                        }))
                }),
                new Function("test", new String[] {"a", "b"}, new Declaration[] {
                        new Declaration(new String[] {"biggest", "result"})
                }, new Statement[] {
                        new IfThenElse(new Comparison(new Variable("a"), Comp.Less, new Variable("b")),
                                new Assignment("biggest", new Variable("b")),
                                new Assignment("biggest", new Variable("a"))),
                        new IfThenElse(new Comparison(new Variable("biggest"), Comp.GreaterEqual, new Number(10)),
                                new Composite(new Statement[] {
                                        new Assignment("biggest", new Unary(Unop.Minus, new Variable("biggest"))),
                                        new IfThen(new Comparison(new Variable("a"), Comp.Equals, new Number(15)),
                                                new Composite(new Statement[] {
                                                        new Assignment("result", new Number(5))
                                                }))
                                }),
                                new Composite(new Statement[] {
                                        new Assignment("biggest", new Unary(Unop.Minus,
                                                new Binary(new Number(10), DivisionOperator, new Binary(new Number(1), Plus, new Binary(new Number(1), Plus, new Number(1))))))
                                })),
                        new While(
                                new BinaryCondition(new UnaryCondition(Bunop.Not, new Comparison(new Variable("biggest"), Comp.Greater, new Number(0))), Bbinop.And, new BinaryCondition(new True(), Bbinop.Or, new BinaryCondition(new False(), Bbinop.Or, new False()))),
                                new Composite(new Statement[] {new Assignment("result", new Binary(new Variable("result"), Plus, new Number(1)))}),
                                false),
                        new Return(new Variable("result"))
                })
        });

        FormatVisitor visitor = new FormatVisitor();
        program.accept(visitor);

        assertEquals("Unexpected code format", "int main() {\n" +
                "  return test(1, 2);\n" +
                "}\n" +
                "\n" +
                "int test(int a, int b) {\n" +
                "  int biggest, result;\n" +
                "  if (a < b)\n" +
                "    biggest = b;\n" +
                "  else\n" +
                "    biggest = a;\n" +
                "  if (biggest >= 10) {\n" +
                "    biggest = -biggest;\n" +
                "    if (a == 15) {\n" +
                "      result = 5;\n" +
                "    }\n" +
                "  } else {\n" +
                "    biggest = -(10 / (1 + (1 + 1)));\n" +
                "  }\n" +
                "  while (!(biggest > 0) && (true || (false || false))) {\n" +
                "    result = result + 1;\n" +
                "  }\n" +
                "  return result;\n" +
                "}", visitor.getFormattedCode());
    }

    @Test
    public void testSimpleDoWhile() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[] {
                        new Declaration(new String[] {"i"})
                }, new Statement[] {
                        new While(new True(), new Assignment("i", new Number(1)), true)
                })
        });

        FormatVisitor visitor = new FormatVisitor();
        program.accept(visitor);

        assertEquals("Unexpected code format", "int main() {\n" +
                "  int i;\n" +
                "  do\n" +
                "    i = 1;\n" +
                "  while (true);\n" +
                "}", visitor.getFormattedCode());
    }

    @Test
    public void testComposite() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[] {
                        new Declaration(new String[] {"i"})
                }, new Statement[] { new Composite(
                        new Statement[]{
                                new Assignment("i", new Binary(new Number(2), Plus, new Number(3)))
                        }
                )})
        });

        FormatVisitor visitor = new FormatVisitor();
        program.accept(visitor);

        assertEquals("Unexpected code format", "int main() {\n" +
                "  int i;\n" +
                "  {\n" +
                "    i = 2 + 3;\n" +
                "  }\n" +
                "}", visitor.getFormattedCode());
    }

}