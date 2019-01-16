package de.andi.minijava;

import de.andi.minijava.assembler.Interpreter;
import de.andi.minijava.assembler.instructions.Instruction;
import de.andi.minijava.codegen.CodeGenerationVisitor;
import de.andi.minijava.codegen.exceptions.*;
import de.andi.minijava.language.Number;
import de.andi.minijava.language.*;
import de.andi.minijava.language.operations.*;
import org.junit.Test;

import static de.andi.minijava.language.operations.Binop.*;
import static org.junit.Assert.assertEquals;

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
                                            new Comparison(new Variable("n"), Comp.Equals, new Number(1)),
                                            new Return(new Number(1))
                                    ),
                                    new Return(new Binary(new Variable("n"), Binop.MultiplicationOperator,
                                            new Call("fak", new Expression[]{new Binary(new Variable("n"), Binop.Minus, new Number(1))})))
                            }),
                            new Function("main", new String[0], new Declaration[0], new Statement[] {
                                    new Return(new Call("fak", new Expression[] {new Number(i)}))
                            })
                    }
            );

            int result = runProgram(program);
            assertEquals("Unexpected result for fak(" + i + ")", expected[i], result);
        }
    }

    @Test
    public void testGreaterThan() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[] {new Declaration(new String[] {"a", "b", "result"})}, new Statement[] {
                        new Assignment("a", new Number(2)),
                        new Assignment("b", new Number(5)),
                        new Assignment("result", new Number(11)),
                        new IfThen(new Comparison(new Variable("b"), Comp.GreaterEqual, new Variable("a")),
                                new Assignment("result", new Number(2))),
                        new Return(new Variable("result"))
                })
        });

        assertEquals("Unexpected result", 2, runProgram(program));
    }

    @Test
    public void testLessThan() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[] {new Declaration(new String[] {"a", "b", "result"})}, new Statement[] {
                        new Assignment("a", new Number(5)),
                        new Assignment("b", new Number(2)),
                        new Assignment("result", new Number(11)),
                        new IfThen(new Comparison(new Variable("b"), Comp.LessEqual, new Variable("a")),
                                new Assignment("result", new Number(2))),
                        new Return(new Variable("result"))
                })
        });

        assertEquals("Unexpected result", 2, runProgram(program));
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
                        new Return(new Call("ggt", new Expression[] {
                                new Number(54),
                                new Number(24)
                        }))
                })
        });

        assertEquals("Unexpected result for ggt(54, 24)", 6, runProgram(program));
    }

    @Test
    public void testSum() {
        Function main = new Function("main",
                new String[] {},
                new Declaration[] {},
                new Statement[] {
                        new Return(new Call("sum", new Expression[] { new Number(10), new Number(15) }))
                });

        Function sum = new Function("sum",
                new String[] {"a", "b"},
                new Declaration[] {},
                new Statement[] {
                        new Return(new Binary(new Variable("a"), Binop.Plus, new Variable("b")))
                });

        Program program = new Program(new Function[] { sum, main });

        assertEquals("Unexpected result for sum(10, 15)", 25, runProgram(program));
    }

    @Test
    public void testCustomExample0() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[0], new Statement[] {
                        new Return(new Call("test", new Expression[] {
                                new Number(15),
                                new Number(8)
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
                                new Composite(new Statement[] {
                                        new Assignment("result", new Binary(new Variable("result"), Plus, new Number(1))),
                                        new Assignment("biggest", new Binary(new Variable("biggest"), Plus, new Number(1)))
                                }),
                                false),
                        // new ExpressionStatement(new Write(new Number(0))),
                        new Return(new Variable("result"))
                })
        });

        assertEquals("Unexpected result", 21, runProgram(program));
    }

    @Test(expected = IllegalFunctionNameException.class)
    public void testDoubleNamedFunction() {
        Program program = new Program(
                new Function[] {
                        new Function("asd", new String[0], new Declaration[0], new Statement[] {
                                new Return(new Number(0))
                        }),
                        new Function("asd", new String[0], new Declaration[0], new Statement[] {
                                new Return(new Number(0))
                        })
                }
        );

        runProgram(program);
    }

    @Test(expected = FunctionNotFoundException.class)
    public void testIllegalMainFunction() {
        Program program = new Program(
                new Function[] {
                        new Function("main", new String[] {"asd"}, new Declaration[0], new Statement[] {
                                new Return(new Number(0))
                        })
                }
        );

        runProgram(program);
    }

    @Test(expected = FunctionNotFoundException.class)
    public void testMissingMainFunction() {
        Program program = new Program(
                new Function[] {
                        new Function("test", new String[] {"asd"}, new Declaration[0], new Statement[] {
                                new Return(new Number(0))
                        })
                }
        );

        runProgram(program);
    }

    @Test(expected = IllegalParameterNameException.class)
    public void testDoublyNamedParameter() {
        Program program = new Program(
                new Function[] {
                        new Function("main", new String[0], new Declaration[0], new Statement[] {
                                new Return(new Number(0))
                        }),
                        new Function("asd", new String[] {"ralf", "olaf", "ralf"}, new Declaration[0], new Statement[] {
                                new Return(new Number(0))
                        })
                }
        );

        runProgram(program);
    }

    @Test(expected = IllegalDeclarationNameException.class)
    public void testDoublyNamedDeclaration() {
        Program program = new Program(
                new Function[] {
                        new Function("main", new String[0], new Declaration[0], new Statement[] {
                                new Return(new Number(0))
                        }),
                        new Function("asd", new String[] {"ralf", "olaf"}, new Declaration[] {
                                new Declaration(new String[] {"fred", "heinz"}),
                                new Declaration(new String[] {"fred"})
                        }, new Statement[] {
                                new Return(new Number(0))
                        })
                }
        );

        runProgram(program);
    }

    @Test(expected = IllegalDeclarationNameException.class)
    public void testDoublyNamedParameterAndDeclaration() {
        Program program = new Program(
                new Function[] {
                        new Function("main", new String[0], new Declaration[0], new Statement[] {
                                new Return(new Number(0))
                        }),
                        new Function("asd", new String[] {"ralf", "olaf"}, new Declaration[] {
                                new Declaration(new String[] {"fred", "heinz"}),
                                new Declaration(new String[] {"olaf"})
                        }, new Statement[] {
                                new Return(new Number(0))
                        })
                }
        );

        runProgram(program);
    }

    @Test(expected = MissingReturnStatementException.class)
    public void testMissingReturnStatement() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[] {new Declaration(new String[] {"a", "b", "result"})}, new Statement[] {
                        new Assignment("a", new Number(2)),
                        new Assignment("b", new Number(5)),
                        new Assignment("result", new Number(11)),
                        new IfThen(new Comparison(new Variable("b"), Comp.GreaterEqual, new Variable("a")),
                                new Assignment("result", new Number(2))),
                        //new Return(new Variable("result"))
                })
        });

        runProgram(program);
    }

    @Test(expected = MissingReturnStatementException.class)
    public void testEmptyFunction() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[0], new Statement[0])
        });

        runProgram(program);
    }

    @Test(expected = VariableNotInitiliazedException.class)
    public void testNotInitializedVariable() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[] {
                        new Declaration(new String[] {"test"})
                }, new Statement[] {
                        new Return(new Variable("test"))
                })
        });

        runProgram(program);
    }

    @Test(expected = VariableNotFoundException.class)
    public void testVariableNotFound() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[0], new Statement[] {
                        new Return(new Call("sum", new Expression[] {
                                new Number(3),
                                new Number(2)
                        }))
                }),
                new Function("sum", new String[] {"a", "b"}, new Declaration[0], new Statement[] {
                        new Return(new Binary(new Variable("c"), Plus, new Variable("b")))
                })
        });

        runProgram(program);
    }

    @Test(expected = FunctionNotFoundException.class)
    public void testUnknownFunctionCall() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[0], new Statement[] {
                        new Return(new Call("test1", new Expression[0]))
                }),
                new Function("test0", new String[0], new Declaration[0], new Statement[] {
                        new Return(new Number(0))
                })
        });

        runProgram(program);
    }

    @Test(expected = BadArgumentSizeException.class)
    public void testBadArgumentNumber() {
        Program program = new Program(new Function[] {
                new Function("main", new String[0], new Declaration[0], new Statement[] {
                        new Return(new Call("sum", new Expression[] {
                                new Number(3),
                                new Number(2),
                                new Number(5)
                        }))
                }),
                new Function("sum", new String[] {"a", "b"}, new Declaration[0], new Statement[] {
                        new Return(new Binary(new Variable("a"), Plus, new Variable("b")))
                })
        });

        runProgram(program);
    }

    private int runProgram(Program program) {
        CodeGenerationVisitor codeGeneration = new CodeGenerationVisitor(program);
        Instruction[] instructions = codeGeneration.getProgram();

        Interpreter interpreter = new Interpreter(instructions);
        return interpreter.execute();
    }

}