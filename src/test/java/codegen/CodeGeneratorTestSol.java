package codegen;

import asm.Interpreter;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@FunctionalInterface
interface TriFunction<A,B,C,R> {
    R apply(A a, B b, C c);
}

public class CodeGeneratorTestSol {

  public void testProgram(Program program, int expectedRetVal) {
    CodeGenerationVisitor cgv = new CodeGenerationVisitor();
    program.accept(cgv);
    int retVal = new Interpreter(cgv.getProgram()).execute();
    assertEquals(expectedRetVal, retVal);
  }
  
  @Test
  public void testSimple() {
    /*
     * 1P
     */
    Program p = new Program(new Function[] { new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
            new Return(new Binary(new Binary(new Number(5), Binop.Minus, new Number(3)), Binop.MultiplicationOperator,
                new Unary(Unop.Minus, new Binary(new Number(3), Binop.Plus, new Number(18)))))     })});
    
    testProgram(p, -42);
  }
  
  @Test
  public void testVariables() {
    /*
     * 2P
     */
    Program p = new Program(new Function[] { new Function("main", new String[] {}, new Declaration[] {
        new Declaration("a"), new Declaration("b"), new Declaration("c"), new Declaration("d")
        }, new Statement[] {
        new Assignment("a", new Number(7)),
        new Assignment("b", new Number(5)),
        new Assignment("c", new Number(3)),
        new Assignment("d", new Number(18)),
        new Assignment("a", new Binary(new Binary(new Variable("a"), Binop.Modulo, new Variable("b")), Binop.MultiplicationOperator,
            new Unary(Unop.Minus, new Binary(new Variable("c"), Binop.Plus, new Variable("d"))))),
        new Return(new Variable("a")) 
     })});

    testProgram(p, -42);
  }
  
  @Test
  public void testSimpleControlflow() {
    /*
     * 2P
     */
    Program p = new Program(new Function[] { new Function("main", new String[] {}, new Declaration[] {new Declaration("i")}, new Statement[] {
        new IfThenElse(new BinaryCondition(new Comparison(new Number(3), Comp.Less, new Number(7)), Bbinop.And, new True()), new Composite(new Statement[] {
          new Assignment("i", new Number(1)),
          new While(new Comparison(new Variable("i"), Comp.Less, new Number(1000)), new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(2))), false),
          new Return(new Variable("i")) 
        }), new Return(new Number(1))),
     })});

    testProgram(p, 1001);
  }
  
  @Test
  public void testFunctionCalls() {
    /*
     * 2P
     */
    Function add = new Function("add", new String[] { "a", "b"}, new Declaration[] {}, new Statement[] {
       new Return(new Binary(new Variable("a"), Binop.Plus, new Variable("b"))) 
    });
    
    Function neg = new Function("neg", new String[] { "a" }, new Declaration[] {}, new Statement[] {
        new Return(new Unary(Unop.Minus, new Variable("a")))
    });
    
    Function sub = new Function("sub", new String[] { "a", "b"}, new Declaration[] {}, new Statement[] {
        new Return(new Binary(new Variable("a"), Binop.Plus, new Call("neg", new Expression[] { new Variable("b") })))
     });
    
    Function f = new Function("f", new String[] { "a", "b", "c", "d", "e", "f"}, new Declaration[] {}, new Statement[] {
        new Return(new Call("add", new Expression[] {
            new Variable("a"),
            new Call("sub", new Expression[] {
                new Variable("b"),
                new Call("sub", new Expression[] {
                  new Variable("c"),
                  new Call("add", new Expression[] {
                    new Variable("d"),
                       new Call("sub", new Expression[] {
                         new Variable("e"),
                         new Call("neg", new Expression[] {
                           new Variable("f"),
                         })
                       })
                  })
                })
            })
        }))
    });
    
    Function main = new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
        new Return(new Call("f", new Expression[] { new Number(2), new Number(4), new Number(8), new Number(16),
            new Number(32), new Number(64) }))    });
    
    Program p1 = new Program(new Function[] { main, add, neg, sub, f });
    Program p2 = new Program(new Function[] { f, add, main, neg, sub });
    Program p3 = new Program(new Function[] { add, f, main, sub, neg });
    
    testProgram(p1, 110);
    testProgram(p2, 110);
    testProgram(p3, 110);
  }

  /*
  @Test
  public void testMulöInterpreter() {
    /*
     * Bitte mit dem Interpreter der Mulö testen oder sicherstellen, dass
     * 
     * 1. keine neuen Instruktionen hinzugefügt wurden,
     * 2. keiner Instruktion mehr Parameter hinzugefügt wurden (insbesondere CMP, -1P) und
     * 3. die Semantik der Instruktionen sonst nicht verändert wurde.
     * /
    
    fail("Bitte mit dem Interpreter der Mulö testen (9.7 und 9.8)!");
  }
  */

  /*
  @Test
  public void testExceptions() {
    /*
     * 0.25 Punkte pro Exception (korrekte Unterklasse, korrekt geworfen), max. 1P insgesamt
     * /
    
    fail("Bitte Exceptions manuell prüfen (9.7 und 9.8)!");
  }
  */

  /*
  @Test
  public void testVisitor() {
    /*
     * Wichtige Punkte beim Visitor-Pattern:
     * 
     * - Genau ein accept() pro konkreter Unterklasse (-0.5 bei mehreren accepts)
     * - Keine Verwendung von instanceof, um durch die Hierarchie zu navigieren
     * - ...
     * 
     * Insg. max. 2 Punkte Abzug, max. 4 Punkte auf dem gesamten Blatt
     * /
    
    fail("Bitte manuell prüfen, dass das Visitor-Pattern korrekt verwendet wurde (9.7 und 9.8)");
  }
  */

  /*
  @Test
  public void testTests() {
    /*
     * 1P Abzug ohne Tests pro Aufgabe (9.7 und 9.8)
     * /
    
    fail("Bitte Tests prüfen!");
  }
  */
  
  @Test
  public void testNest() {
    /*
     * 1P Abzug, falls dieser Test scheitert (ab insg. <= 4 Punkten bei dieser Aufgabe kein Abzug mehr)
     */
    Program p = new Program(new Function[] { new Function("main", new String[] {}, new Declaration[] {new Declaration("i"), new Declaration("j"), new Declaration("k")}, new Statement[] {
        new IfThenElse(new BinaryCondition(new Comparison(new Number(3), Comp.Less, new Number(7)), Bbinop.And, new True()), new Composite(new Statement[] {
          new Assignment("i", new Number(1)),
          new Assignment("j", new Number(2)),
          new Assignment("k", new Number(3)),
          new While(new Comparison(new Variable("i"), Comp.Less, new Number(1000)), new Composite(new Statement[] {
              new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1))),
              new While(new Comparison(new Variable("j"), Comp.LessEqual, new Variable("i")), new Composite(new Statement[] {
                new Assignment("j", new Binary(new Variable("j"), Binop.Plus, new Number(1))),
                new While(new Comparison(new Variable("k"), Comp.LessEqual, new Variable("i")), new Composite(new Statement[] {
                  new Assignment("k", new Binary(new Variable("k"), Binop.MultiplicationOperator, new Number(2))),
                }), false)
              }), false),
          }), false),
          new Return(new Binary(new Variable("i"), Binop.Plus, new Binary(new Variable("j"), Binop.Plus, new Variable("k")))) 
        }), new Return(new Number(1))),
     })});

    testProgram(p, 3537);
  }
  
  @Test
  public void testMainCallable() {
    /*
     * Testet, ob main() selbst aufrufbar ist. Ergibt "0.5 lokale Bonuspunkte", mit denen man Fehler ausgleichen
     * kann. Die Maximalpunktzahl der Aufgabe 9.7 lässt sich so nicht überschreiten.
     */
    
    Program p = new Program(
        new Function[] { new Function("main", new String[] {}, new Declaration[] { new Declaration("i") },
            new Statement[] { new IfThenElse(new Comparison(new Read(), Comp.NotEquals, new Number(0)),
                new Return(new Number(37)),
                new Composite(new Statement[] {
                    new Return(new Binary(new Call("main", new Expression[] {}), Binop.Plus, new Number(5))) })) }) });

    InputStream in = System.in;
    try {
      String input = "0\n1\n";
      System.setIn(new ByteArrayInputStream(input.getBytes()));
      testProgram(p, 42);
    } finally {
      System.setIn(in);
    }
  }
  
  @Test
  public void testFib() {
    /*
     * 1P Abzug, falls dieser Test scheitert (ab insg. <= 4 Punkten bei dieser Aufgabe kein Abzug mehr)
     */
    java.util.function.Function<Integer, Program> getFibProgram = (n) -> {
      Function crazyFib = new Function("crazyFib", new String[] { "n" },
          new Declaration[] { new Declaration("first", "second", "i", "temp") }, new Statement[] {
              new IfThen(new Comparison(new Variable("n"), Comp.LessEqual, new Number(1)), new Return(new Variable("n"))),
              new Assignment("first", new Number(0)),
              new Assignment("second", new Number(1)),
              new Assignment("i", new Number(1)),
              new While(new Comparison(new Variable("i"), Comp.Less, new Variable("n")), new Composite(new Statement[] {
                new Assignment("temp", new Variable("first")),
                new Assignment("first", new Binary(new Variable("first"), Binop.Plus, new Variable("second"))),
                new Assignment("second", new Variable("temp")),
                new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
              }), false),
              new Return(new Binary(new Variable("first"), Binop.Plus, new Call("crazyFib", new Expression[] { new Binary(new Variable("n"), Binop.Minus, new Number(2)) })))
          });
      Function main = new Function("main", new String[] {}, new Declaration[] {},
          new Statement[] {new Return(new Call("crazyFib", new Expression[] { new Number(n)} ))});
      return new Program(new Function[] { main, crazyFib });
    };

    testProgram(getFibProgram.apply(0), 0);
    testProgram(getFibProgram.apply(1), 1);
    testProgram(getFibProgram.apply(4), 3);
    testProgram(getFibProgram.apply(10), 55);
    testProgram(getFibProgram.apply(30), 832040);
  }
  
  @Test
  public void testCompare() {
    /*
     * 1P Abzug, falls dieser Test scheitert (ab insg. <= 4 Punkten bei dieser Aufgabe kein Abzug mehr)
     */
    TriFunction<Integer, Comp, Integer, Program> getProgram = (a, comp, b) -> {
      Function main = new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
          new IfThenElse(new Comparison(new Number(a), comp, new Number(b)),
              new Return(new Number(1)),
              new Return(new Number(0))) 
       });
      return new Program(new Function[] { main });
    };
    
    testProgram(getProgram.apply(1, Comp.Equals, 1), 1);
    testProgram(getProgram.apply(1, Comp.Equals, 0), 0);
    testProgram(getProgram.apply(0, Comp.Equals, 1), 0);

    testProgram(getProgram.apply(1, Comp.NotEquals, 1), 0);
    testProgram(getProgram.apply(1, Comp.NotEquals, 0), 1);
    testProgram(getProgram.apply(0, Comp.NotEquals, 1), 1);
    
    testProgram(getProgram.apply(1, Comp.LessEqual, 1), 1);
    testProgram(getProgram.apply(10, Comp.LessEqual, 100), 1);
    testProgram(getProgram.apply(100, Comp.LessEqual, 3), 0);
    
    testProgram(getProgram.apply(100, Comp.Less, 100), 0);
    testProgram(getProgram.apply(-10, Comp.Less, 100), 1);
    testProgram(getProgram.apply(100, Comp.Less, -3), 0);
    
    testProgram(getProgram.apply(1, Comp.GreaterEqual, 1), 1);
    testProgram(getProgram.apply(10, Comp.GreaterEqual, 100), 0);
    testProgram(getProgram.apply(100, Comp.GreaterEqual, 3), 1);
    
    testProgram(getProgram.apply(100, Comp.Greater, 100), 0);
    testProgram(getProgram.apply(-10, Comp.Greater, 100), 0);
    testProgram(getProgram.apply(100, Comp.Greater, -3), 1);
  }
  
  @Test
  public void testExprStmt() {
    /*
     * 0.5P Abzug, falls dieser Test scheitert (ab insg. <= 4 Punkten bei dieser Aufgabe kein Abzug mehr)
     */
    Function foo = new Function("foo", new String[] {}, new Declaration[] {}, new Statement[] {
       new Return(new Number(0))
    });
    Function main = new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
      new ExpressionStatement(new Number(42)),
      new ExpressionStatement(new Write(new Number(5))),
      new ExpressionStatement(new Call("foo", new Expression[] {})),
      new Return(new Number(0))
    });
    Program p = new Program(new Function[] { main, foo });
    
    testProgram(p, 0);
    
    // works: fail("Prüfen, dass 5 ausgegeben wurde");
  }
  
  @Test
  public void testDoWhile() {
    /*
     * 0.5P Abzug, falls dieser Test scheitert (ab insg. <= 4 Punkten bei dieser Aufgabe kein Abzug mehr)
     */
    java.util.function.Function<Boolean, Program> getProgram = (doWhile) -> {
      Function main = new Function("main", new String[] {}, new Declaration[] {new Declaration("i"), new Declaration("result")}, new Statement[] {
          new Assignment("i", new Number(5)),
          new Assignment("result", new Number(1)),
          new While(new Comparison(new Binary(new Variable("i"), Binop.MultiplicationOperator, new Variable("i")), Comp.Less, new Number(20)), new Composite(new Statement[] {
              new Assignment("result", new Binary(new Variable("result"), Binop.MultiplicationOperator, new Number(2))),
              new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
          }), doWhile),
          new Return(new Variable("result"))
       });
      return new Program(new Function[] { main });
    };
    
    testProgram(getProgram.apply(true), 2);
    testProgram(getProgram.apply(false), 1);
  }
  
  @Test
  public void testGGT() {
    BiFunction<Integer, Integer, Program> getGgtProgram = (a, b) -> {
      Statement ggtSwap =
          new IfThen(new Comparison(new Variable("b"), Comp.Greater, new Variable("a")),
              new Composite(new Statement[] {new Assignment("temp", new Variable("a")),
                  new Assignment("a", new Variable("b")),
                  new Assignment("b", new Variable("temp")),}));
      Statement ggtWhile = new While(new Comparison(new Variable("b"), Comp.NotEquals, new Number(0)),
          new Composite(new Statement[] {new Assignment("temp", new Variable("b")),
              new Assignment("b", new Binary(new Variable("a"), Binop.Modulo, new Variable("b"))),
              new Assignment("a", new Variable("temp"))}), false);
      Function ggt = new Function("ggt", new String[] {"a", "b"},
          new Declaration[] {new Declaration("temp")},
          new Statement[] {ggtSwap, ggtWhile, new Return(new Variable("a"))});
      Function mainFunctionGgt =
          new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
              new Return(new Call("ggt", new Expression[] {new Number(a), new Number(b)}))});
      Program ggtProgram = new Program(new Function[] {ggt, mainFunctionGgt});
      return ggtProgram;
    };
    
    testProgram(getGgtProgram.apply(12, 18), 6);
    testProgram(getGgtProgram.apply(16, 175), 1);
    testProgram(getGgtProgram.apply(144, 160), 16);
    testProgram(getGgtProgram.apply(3780, 3528), 252);
    testProgram(getGgtProgram.apply(3528, 3780), 252);
    testProgram(getGgtProgram.apply(378000, 3528), 504);
    testProgram(getGgtProgram.apply(3528, 378000), 504);
  }

  @Test
  public void testFak() {
    java.util.function.Function<Integer, Program> getFakProgram = (n) -> {
      Statement fakRecEnd = new IfThen(new Comparison(new Variable("n"), Comp.Equals, new Number(0)),
          new Return(new Number(1)));
      Statement fakRec =
          new Return(new Binary(new Variable("n"), Binop.MultiplicationOperator, new Call("fak",
              new Expression[] {new Binary(new Variable("n"), Binop.Minus, new Number(1))})));
      Function fakFunc = new Function("fak", new String[] {"n"}, new Declaration[] {},
          new Statement[] {fakRecEnd, fakRec});
      Function mainFunctionFak = new Function("main", new String[] {}, new Declaration[] {},
          new Statement[] {new Return(new Call("fak", new Expression[] {new Number(n)}))});
      Program fakProgram = new Program(new Function[] {mainFunctionFak, fakFunc});
      return fakProgram;
    };
    
    testProgram(getFakProgram.apply(3), 6);
    testProgram(getFakProgram.apply(10), 3628800);
  }
  
  /*
   * Aufgabe 9.8 (Switch)
   */
  
  @Test
  public void SwitchSheetExample() {
    BiFunction<Integer, Integer, Program> getSwitch = (x, y) -> {
    return new Program(new Function[] {new Function("main", new String[] {},
        new Declaration[] { new Declaration("x", "y", "z") },
        new Statement[] {
                new Assignment("x", new Number(x)),
                new Assignment("z", new Number(0)),
                new Switch(new SwitchCase[] {
                        new SwitchCase(new Number(1),
                                new Composite(new Statement[] {
                                        new Assignment("y", new Number(y)),
                                        new Switch(new SwitchCase[] {
                                                new SwitchCase(new Number(1),
                                                        new Composite(new Statement[] {
                                                                new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(1))),
                                                                new Break()
                                                        })),
                                                new SwitchCase(new Number(2), 
                                                        new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(2)))
                                                )
                                        }, 
                                                null,
                                                new Variable("y")),
                                        new Break()
                                })),
                        new SwitchCase(new Number(2),
                                new Assignment("z", new Number(42)))
                    },
                    new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(1))),
                        new Variable("x")),
                new Return(new Variable("z"))
        })});
    };
    
    testProgram(getSwitch.apply(1, 1), 1);
    testProgram(getSwitch.apply(1, 2), 2);
    testProgram(getSwitch.apply(1, 3), 0);
    testProgram(getSwitch.apply(2, 3), 43);
    testProgram(getSwitch.apply(30, 3), 1);
  }
  
  @Test
  public void testSwitchSimpleReturn() {
    /*
     * 1P
     */
    java.util.function.Function<Integer, Program> getSwitch = (a) -> {
      Function main = new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
          new Switch(new SwitchCase[] {
              new SwitchCase(new Number(1), new Return(new Number(42))),
              new SwitchCase(new Number(5), new Return(new Number(100))),
              new SwitchCase(new Number(8), new Return(new Number(33))),
          }, new Return(new Number(55)), new Number(a))
       });
      return new Program(new Function[] { main });
    };
    
    testProgram(getSwitch.apply(1), 42);
    testProgram(getSwitch.apply(5), 100);
    testProgram(getSwitch.apply(8), 33);
    testProgram(getSwitch.apply(100), 55);
  }
  
  @Test
  @Ignore("Works only when called individually")
  public void testSwitchSingleEvaluation() {
    /*
     * 1P
     */
    Function main = new Function("main", new String[] {}, new Declaration[] {new Declaration("a")}, new Statement[] {
        new Assignment("a", new Number(0)),
        new Switch(new SwitchCase[] {
            new SwitchCase(new Number(1), new Assignment("a", new Number(42))),
            new SwitchCase(new Number(5), new Composite(new Statement[] {
                new Assignment("a", new Binary(new Variable("a"), Binop.Plus, new Number(100))),
                new Break()
            })),
            new SwitchCase(new Number(8), new Composite(new Statement[] {
                new Assignment("a", new Number(30)),
                new Break()
            })),
        }, new Assignment("a", new Number(55)), new Read()),
        new Return(new Number(0))
     });
    Program p = new Program(new Function[] { main });
    
    InputStream in = System.in;
    try {
      String input = "0\n";
      System.setIn(new ByteArrayInputStream(input.getBytes()));
      testProgram(p, 0);
    } catch(NullPointerException ex) {
      fail("read() wird vmtl. mehrfach aufgerufen.");
    } finally {
      System.setIn(in);
    }
  }
  
  @Test
  public void testSwitchBreak() {
    /*
     * 1P
     */
    java.util.function.Function<Integer, Program> getSwitch = (a) -> {
      Function main = new Function("main", new String[] {}, new Declaration[] {new Declaration("a")}, new Statement[] {
          new Assignment("a", new Number(0)),
          new Switch(new SwitchCase[] {
              new SwitchCase(new Number(1), new Assignment("a", new Number(42))),
              new SwitchCase(new Number(5), new Composite(new Statement[] {
                  new Assignment("a", new Binary(new Variable("a"), Binop.Plus, new Number(100))),
                  new Break()
              })),
              new SwitchCase(new Number(8), new Composite(new Statement[] {
                  new Assignment("a", new Number(30)),
                  new Break()
              })),
          }, new Assignment("a", new Number(55)), new Number(a)),
          new Return(new Variable("a"))
       });
      return new Program(new Function[] { main });
    };
    
    testProgram(getSwitch.apply(1), 142);
    testProgram(getSwitch.apply(5), 100);
    testProgram(getSwitch.apply(8), 30);
    testProgram(getSwitch.apply(100), 55);
  }
  
  @Test
  public void testSwitchNested() {
    /*
     * 2P
     */
    TriFunction<Integer, Integer, Integer, Program> getSwitch = (x, y, k) -> {
    return new Program(new Function[] {new Function("main", new String[] {},
        new Declaration[] { new Declaration("x", "y", "z", "k") },
        new Statement[] {
                new Assignment("x", new Number(x)),
                new Assignment("z", new Number(0)),
                new Assignment("k", new Number(k)),
                new Switch(new SwitchCase[] {
                        new SwitchCase(new Number(1),
                                new Composite(new Statement[] {
                                        new Assignment("y", new Number(y)),
                                        new Switch(new SwitchCase[] {
                                                new SwitchCase(new Number(1),
                                                        new Composite(new Statement[] {
                                                                new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(1))),
                                                                new Switch(new SwitchCase[] {
                                                                  new SwitchCase(new Number(1), new Composite(new Statement[] {
                                                                    new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(5))),
                                                                    new Break()
                                                                  })),
                                                                  new SwitchCase(new Number(2), new Composite(new Statement[] {
                                                                      new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(8))),
                                                                      new Break()
                                                                  }))
                                                                }, null, new Variable("k")),
                                                                new Break()
                                                        })),
                                                new SwitchCase(new Number(2), 
                                                        new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(2)))
                                                )
                                        }, 
                                                null,
                                                new Variable("y")),
                                        new Break()
                                })),
                        new SwitchCase(new Number(2), new Composite(new Statement[] {
                                new Assignment("z", new Number(42)),
                                new Assignment("y", new Number(y)),
                                new Switch(new SwitchCase[] {
                                    new SwitchCase(new Number(1),
                                            new Composite(new Statement[] {
                                                    new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(1))),
                                                    new Switch(new SwitchCase[] {
                                                      new SwitchCase(new Number(3), new Composite(new Statement[] {
                                                        new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(5))),
                                                      })),
                                                      new SwitchCase(new Number(4), new Composite(new Statement[] {
                                                          new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(8))),
                                                          new Break()
                                                      }))
                                                    }, null, new Variable("k")),
                                                    new Break()
                                            })),
                                    new SwitchCase(new Number(2), 
                                            new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(2)))
                                    )
                            }, 
                                    null,
                                    new Binary(new Variable("y"), Binop.Plus, new Number(2)))
                        }))
                    },
                    new Assignment("z", new Binary(new Variable("z"), Binop.Plus, new Number(1))),
                        new Variable("x")),
                new Return(new Variable("z"))
        })});
    };
    
    testProgram(getSwitch.apply(1, 1, 5), 1);
    testProgram(getSwitch.apply(1, 2, 5), 2);
    testProgram(getSwitch.apply(1, 3, 5), 0);
    testProgram(getSwitch.apply(2, 3, 5), 43);
    testProgram(getSwitch.apply(30, 3, 5), 1);
    testProgram(getSwitch.apply(1, 1, 1), 6);
    testProgram(getSwitch.apply(1, 1, 2), 9);
    testProgram(getSwitch.apply(2, -1, 3), 57);
    testProgram(getSwitch.apply(2, -1, 4), 52);
    testProgram(getSwitch.apply(2, 0, 4), 45);
  }
  
  @Test
  public void testSwitchNoCrosstalk() {
    /*
     * 1P Abzug, falls dieser Test scheitert (ab insg. <= 2 Punkten bei dieser Aufgabe kein Abzug mehr)
     */
    Program p1 = new Program(new Function[] { new Function("main", new String[] {}, new Declaration[] {new Declaration("a")}, new Statement[] {
        new Assignment("a", new Number(0)),
        new Switch(new SwitchCase[] {
            new SwitchCase(new Number(5), new Composite(new Statement[] {
                new Assignment("a", new Number(1)),
                new Break()
            })),
            new SwitchCase(new Number(0), new Composite(new Statement[] {
                new Assignment("a", new Number(5)),
                new Break()
            })),
            new SwitchCase(new Number(8), new Composite(new Statement[] {
                new Assignment("a", new Number(30)),
                new Break()
            })),
        }, null, new Variable("a")),
        new Return(new Variable("a"))
     })});

    testProgram(p1, 5);
    
    Program p2 = new Program(new Function[] { new Function("main", new String[] {}, new Declaration[] {new Declaration("a")}, new Statement[] {
        new Assignment("a", new Number(0)),
        new Switch(new SwitchCase[] {
            new SwitchCase(new Number(8), new Composite(new Statement[] {
                new Assignment("a", new Number(1)),
                new Break()
            })),
            new SwitchCase(new Number(0), new Composite(new Statement[] {
                new Assignment("a", new Number(5)),
                new Break()
            })),
            new SwitchCase(new Number(5), new Composite(new Statement[] {
                new Assignment("a", new Number(30)),
                new Break()
            })),
        }, null, new Variable("a")),
        new Return(new Variable("a"))
     })});
    
    testProgram(p2, 5);
    
    Program p3 = new Program(new Function[] { new Function("main", new String[] {}, new Declaration[] {new Declaration("a")}, new Statement[] {
        new Assignment("a", new Number(0)),
        new Switch(new SwitchCase[] {
            new SwitchCase(new Number(5), new Composite(new Statement[] {
                new Assignment("a", new Number(1)),
            })),
            new SwitchCase(new Number(0), new Composite(new Statement[] {
                new Assignment("a", new Number(5)),
            })),
        }, null, new Variable("a")),
        new Return(new Variable("a"))
     })});
    
    testProgram(p3, 5);
  }
}
