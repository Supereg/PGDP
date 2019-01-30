package codegen;

import asm.Interpreter;
import codegen.CodeGeneratorTestSol.TriFunction;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class CodeGeneratorTestSol2 {

  private void testProgram(Program program, int expectedRetVal) {
    CodeGenerationVisitor cgv = new CodeGenerationVisitor();
    program.accept(cgv);
    int retVal = new Interpreter(cgv.getProgram()).execute();
    assertEquals(expectedRetVal, retVal);
  }
  
  @Test
  public void testSimpleArray() {
    /*
     * 0.5P
     */
    
    int length = 3;
    Program p = new Program(new Function("main", new String[] {},
        new Declaration[] { new Declaration(Type.IntArray, "a"), new Declaration("sum"), new Declaration("i") },
        new Statement[] {
      new Assignment("a", new ArrayAllocator(new Number(length))),
      new ArrayIndexAssignment(new Variable("a"), new Number(0), new Number(2)),
      new ArrayIndexAssignment(new Variable("a"), new Number(1), new Number(4)),
      new ArrayIndexAssignment(new Variable("a"), new Number(2), new Number(8)),
      new Assignment("sum", new Number(0)),
      new Assignment("i", new Number(0)),
      new While(new Comparison(new Variable("i"), Comp.Less, new Number(length)), new Composite(new Statement[] {
        new Assignment("sum", new Binary(new Variable("sum"), Binop.Plus, new ArrayAccess(new Variable("a"), new Variable("i")))),
        new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
      }), false),
      new Return(new Variable("sum"))
    }));
    
    testProgram(p, 14);
  }
  
  @Test
  public void testArrayExpr() {
    /*
     * 1P
     */

    int i = 5;
    int j = 3;
    
    Function init = new Function(Type.IntArray, "init", new Parameter[] { new Parameter(Type.Int, "size") },
        new Declaration[] { new Declaration("i"), new Declaration(Type.IntArray, "array") }, new Statement[] {
            new Assignment("i", new Number(0)),
            new Assignment("array", new ArrayAllocator(new Variable("size"))),
            new While(new Comparison(new Variable("i"), Comp.Less, new Variable("size")), new Composite(new Statement[] {
                new ArrayIndexAssignment(new Variable("array"), new Variable("i"), new Binary(new Binary(new Variable("i"), Binop.Plus, new Number(1)), Binop.MultiplicationOperator, new Binary(new Variable("i"), Binop.Plus, new Number(1)))),
                new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
            }), false),
            new Return(new Variable("array"))
        });
    
    Function main = new Function("main", new String[] {},
        new Declaration[] { new Declaration(Type.IntArray, "a") },
        new Statement[] {
      new Return( new ArrayAccess(new Call("init", new Number(25)), new Binary(new Number(i), Binop.Plus, new Number(j)) ))
    });
    
    Program p = new Program(init, main);
    
//    FormatVisitor fv = new FormatVisitor();
//    p.accept(fv);
//    System.out.println(fv.getFormattedCode());
    
    testProgram(p, (i + j + 1)*(i + j + 1));
  }
  
  @Test
  public void testArrayLength() {
    /*
     * 2P
     */
    
    final int size = 25;
    
    Function init = new Function(Type.IntArray, "init", new Parameter[] { new Parameter(Type.Int, "size") },
        new Declaration[] { new Declaration("i"), new Declaration(Type.IntArray, "array") }, new Statement[] {
            new Assignment("i", new Number(0)),
            new Assignment("array", new ArrayAllocator(new Variable("size"))),
            new While(new Comparison(new Variable("i"), Comp.Less, new Variable("size")), new Composite(new Statement[] {
                new ArrayIndexAssignment(new Variable("array"), new Variable("i"), new Binary(new Binary(new Variable("i"), Binop.Plus, new Number(1)), Binop.MultiplicationOperator, new Binary(new Variable("i"), Binop.Plus, new Number(1)))),
                new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
            }), false),
            new Return(new Variable("array"))
        });
    
    Function sumRec = new Function(Type.Int, "sumRec", new Parameter[] { new Parameter(Type.IntArray, "array"), new Parameter(Type.Int, "i") },
        new Declaration[] { }, new Statement[] {
            new IfThen(new Comparison(new Variable("i"), Comp.GreaterEqual, new ArrayLength(new Variable("array"))), new Return(new Number(0))),
            new Return(new Binary(new ArrayAccess(new Variable("array"), new Variable("i")), Binop.Plus, new Call("sumRec", new Variable("array"), new Binary(new Variable("i"), Binop.Plus, new Number(1))))),
        });

    Function sum = new Function(Type.Int, "sum", new Parameter[] { new Parameter(Type.IntArray, "array") },
        new Declaration[] { }, new Statement[] {
            new Return(new Call("sumRec", new Variable("array"), new Number(0)))
        });

    
    Function main = new Function("main", new String[] {},
        new Declaration[] { new Declaration(Type.IntArray, "a") },
        new Statement[] {
      new Assignment("a", new Call("init", new Number(size))),
      new Return(new Call("sum", new Variable("a")))
    });
    
    Program p = new Program(init, sum, sumRec, main);
    
    testProgram(p, (size*(size + 1)*(2*size + 1))/6);
  }
  
  @Test
  public void testMergesort() {
    /*
     * 3P
     */
    
    int[] toSort = { 1, 7, 3, -1, 5, 9, 100, 22, 33, 100, 88, -10, 33, -12, 432, 0, 5 };
    
    java.util.function.Function<Integer, Program> getProgram = (index) -> {
      Function main = new Function(Type.Int, "main", new Parameter[] {},
          new Declaration[] { new Declaration(Type.IntArray, "arr") },
          new Statement[] {
              new Assignment("arr", new ArrayAllocator(new Number(toSort.length))), new Composite(
                  IntStream.range(0, toSort.length).boxed().map(
                      i -> new ArrayIndexAssignment(new Variable("arr"), new Number(i), new Number(toSort[i]))).toArray(Statement[]::new)
              ),
              new ExpressionStatement(
                  new Call("mergeSort",
                          new Variable("arr"), new Number(0), new Binary(new ArrayLength(new Variable("arr")), Binop.Minus, new Number(1)))),
              new Return(new ArrayAccess(new Variable("arr"), new Number(index))) });

      Function mergeSort = new Function(Type.IntArray, "mergeSort",
          new Parameter[] { new Parameter(Type.IntArray, "arr"), new Parameter(Type.Int, "low"), new Parameter(Type.Int, "high") },
          new Declaration[] { new Declaration(Type.Int, "mid") },
          new Statement[] {
              new IfThen(new Comparison(new Variable("high"), Comp.LessEqual, new Variable("low")), new Return(new Number(0))),
              new Assignment("mid", new Binary(new Binary(new Variable("low"), Binop.Plus, new Variable("high")), Binop.DivisionOperator, new Number(2))),
              new ExpressionStatement(new Call("mergeSort", new Variable("arr"), new Variable("low"), new Variable("mid"))),
              new ExpressionStatement(new Call("mergeSort", new Variable("arr"), new Binary(new Variable("mid"), Binop.Plus, new Number(1)), new Variable("high"))),
              new ExpressionStatement(new Call("merge", new Variable("arr"), new Variable("low"), new Variable("mid"), new Variable("high"))),
              new Return(new Number(0))
          });

      Function merge = new Function(Type.IntArray, "merge",
          new Parameter[] { new Parameter(Type.IntArray, "arr"),
              new Parameter(Type.Int, "low"), new Parameter(Type.Int, "mid"),
              new Parameter(Type.Int, "high") },
          new Declaration[] { new Declaration("i", "j", "k", "size"),
              new Declaration(Type.IntArray, "helperArr")},
          new Statement[] {
              new Assignment("i", new Variable("low")),
              new Assignment("j", new Binary(new Variable("mid"), Binop.Plus, new Number(1))),
              new Assignment("k", new Number(0)),
              new Assignment("helperArr", new ArrayAllocator(new Binary(new Binary(new Variable("high"), Binop.Minus, new Variable("low")), Binop.Plus, new Number(1)))),
              new While(
                  new BinaryCondition(new Comparison(new Variable("i"), Comp.LessEqual, new Variable("mid")),
                      Bbinop.And,
                      new Comparison(new Variable("j"), Comp.LessEqual, new Variable("high"))),
                  new IfThenElse(
                          new Comparison(new ArrayAccess(new Variable("arr"),
                              new Variable("i")), Comp.Less,
                              new ArrayAccess(new Variable("arr"), new Variable("j"))),
                          new Composite(
                              new Statement[] {
                                  new ArrayIndexAssignment(new Variable("helperArr"),
                                      new Variable("k"),
                                      new ArrayAccess(new Variable("arr"),
                                          new Variable("i"))),
                                  new Assignment("k",
                                      new Binary(new Variable("k"), Binop.Plus,
                                          new Number(1))),
                                  new Assignment("i",
                                      new Binary(new Variable("i"), Binop.Plus,
                                          new Number(1))) }),
                          new Composite(
                              new Statement[] {
                                  new ArrayIndexAssignment(new Variable("helperArr"),
                                      new Variable("k"), new ArrayAccess(
                                          new Variable("arr"), new Variable("j"))),
                                  new Assignment("k",
                                      new Binary(new Variable("k"), Binop.Plus,
                                          new Number(1))),
                                  new Assignment("j",
                                      new Binary(new Variable("j"), Binop.Plus,
                                          new Number(1))) })),
                  false),
              new While(new Comparison(new Variable("i"), Comp.LessEqual, new Variable("mid")),
                  new Composite(new Statement[] {
                      new ArrayIndexAssignment(new Variable("helperArr"), new Variable("k"),
                          new ArrayAccess(new Variable("arr"), new Variable("i"))),
                      new Assignment("k", new Binary(new Variable("k"), Binop.Plus, new Number(1))),
                      new Assignment("i",
                          new Binary(new Variable("i"), Binop.Plus, new Number(1))) }),
                  false),
              new While(new Comparison(new Variable("j"), Comp.LessEqual, new Variable("high")),
                  new Composite(new Statement[] {
                      new ArrayIndexAssignment(new Variable("helperArr"), new Variable("k"),
                          new ArrayAccess(new Variable("arr"), new Variable("j"))),
                      new Assignment("k", new Binary(new Variable("k"), Binop.Plus, new Number(1))),
                      new Assignment("j",
                          new Binary(new Variable("j"), Binop.Plus, new Number(1))) }),
                  false),
              new Assignment("i", new Number(0)),
              new While(new Comparison(new Variable("i"), Comp.Less, new ArrayLength(new Variable("helperArr"))),
                  new Composite(new Statement[] {
                      new ArrayIndexAssignment(new Variable("arr"), new Binary(new Variable("low"), Binop.Plus, new Variable("i")),
                          new ArrayAccess(new Variable("helperArr"), new Variable("i"))),
                      new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
                      }),
                  false),
              new Return(new Number(0)) });
      return new Program(main, mergeSort, merge);
    };
    
    int[] sorted = toSort.clone();
    Arrays.sort(sorted);
    
    for (int i = 0; i < sorted.length; i++)
      testProgram(getProgram.apply(i), sorted[i]);
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
     *
    
    fail("Bitte mit dem Interpreter der Mulö testen!");
  */

  /*
  @Test
  public void testTests() {
    /*
     * 1P Abzug ohne Tests pro Aufgabe (zusammen mit Interpreter)
     * 
     * Achtung: Die Tests müssen JUnit verwenden!
     /
    
    fail("Bitte JUnit-Tests prüfen!");
  }
  */
  
  /*
   * Alte Tests
   */

  @Ignore("Works only when called individually")
  @Test
  public void testArraySheetExample() {
    Function init = new Function(Type.IntArray, "init", new Parameter[] { new Parameter(Type.Int, "size") },
        new Declaration[] { new Declaration("i"), new Declaration(Type.IntArray, "array") }, new Statement[] {
            new Assignment("i", new Number(0)),
            new Assignment("array", new ArrayAllocator(new Variable("size"))),
            new While(new Comparison(new Variable("i"), Comp.Less, new Variable("size")), new Composite(new Statement[] {
                new ArrayIndexAssignment(new Variable("array"), new Variable("i"), new Variable("i")),
                new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
            }), false),
            new Return(new Variable("array"))
        });
    
    Function sum = new Function(Type.Int, "sum", new Parameter[] { new Parameter(Type.IntArray, "array") },
        new Declaration[] { new Declaration("i"), new Declaration("sum") }, new Statement[] {
            new Assignment("i", new Number(0)),
            new Assignment("sum", new Number(0)),
            new While(new Comparison(new Variable("i"), Comp.Less, new ArrayLength(new Variable("array"))), new Composite(new Statement[] {
                new Assignment("sum", new Binary(new Variable("sum"), Binop.Plus, new ArrayAccess(new Variable("array"), new Variable("i")))),
                new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
            }), false),
            new Return(new Variable("sum"))
        });
    
    Function main = new Function("main", new String[] {},
        new Declaration[] { new Declaration(Type.IntArray, "a") },
        new Statement[] {
      new Assignment("a", new Call("init", new Read())),
      new Return(new Call("sum", new Variable("a")))
    });
    
    Program p = new Program(init, sum, main);
    
    InputStream in = System.in;
    try {
      String input = "10\n";
      System.setIn(new ByteArrayInputStream(input.getBytes()));
      testProgram(p, 45);
    } finally {
      System.setIn(in);
    }
  }
  
  @Test
  public void testSimple() {
    Program p = new Program(new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
            new Return(new Binary(new Binary(new Number(5), Binop.Minus, new Number(3)), Binop.MultiplicationOperator,
                new Unary(Unop.Minus, new Binary(new Number(3), Binop.Plus, new Number(18)))))     }));
    
    testProgram(p, -42);
  }
  
  @Test
  public void testVariables() {
    Program p = new Program(new Function("main", new String[] {}, new Declaration[] {
        new Declaration("a"), new Declaration("b"), new Declaration("c"), new Declaration("d")
        }, new Statement[] {
        new Assignment("a", new Number(7)),
        new Assignment("b", new Number(5)),
        new Assignment("c", new Number(3)),
        new Assignment("d", new Number(18)),
        new Assignment("a", new Binary(new Binary(new Variable("a"), Binop.Modulo, new Variable("b")), Binop.MultiplicationOperator,
            new Unary(Unop.Minus, new Binary(new Variable("c"), Binop.Plus, new Variable("d"))))),
        new Return(new Variable("a"))
     }));

    testProgram(p, -42);
  }
  
  @Test
  public void testSimpleControlflow() {
    Program p = new Program(new Function("main", new String[] {}, new Declaration[] {new Declaration("i")}, new Statement[] {
        new IfThenElse(new BinaryCondition(new Comparison(new Number(3), Comp.Less, new Number(7)), Bbinop.And, new True()), new Composite(new Statement[] {
          new Assignment("i", new Number(1)),
          new While(new Comparison(new Variable("i"), Comp.Less, new Number(1000)), new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(2))), false),
          new Return(new Variable("i"))
        }), new Return(new Number(1))),
     }));

    testProgram(p, 1001);
  }
  
  @Test
  public void testFunctionCalls() {
    Function add = new Function("add", new String[] { "a", "b"}, new Declaration[] {}, new Statement[] {
       new Return(new Binary(new Variable("a"), Binop.Plus, new Variable("b"))) 
    });
    
    Function neg = new Function("neg", new String[] { "a" }, new Declaration[] {}, new Statement[] {
        new Return(new Unary(Unop.Minus, new Variable("a")))
    });
    
    Function sub = new Function("sub", new String[] { "a", "b"}, new Declaration[] {}, new Statement[] {
        new Return(new Binary(new Variable("a"), Binop.Plus, new Call("neg", new Variable("b"))))
     });
    
    Function f = new Function("f", new String[] { "a", "b", "c", "d", "e", "f"}, new Declaration[] {}, new Statement[] {
        new Return(new Call("add", new Variable("a"),
                new Call("sub", new Variable("b"),
                        new Call("sub", new Variable("c"),
                                new Call("add", new Variable("d"),
                                        new Call("sub", new Variable("e"),
                                                new Call("neg", new Variable("f"))))))))
    });
    
    Function main = new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
        new Return(new Call("f", new Number(2), new Number(4), new Number(8), new Number(16),
                new Number(32), new Number(64)))    });
    
    Program p1 = new Program(main, add, neg, sub, f);
    Program p2 = new Program(f, add, main, neg, sub);
    Program p3 = new Program(add, f, main, sub, neg);
    
    testProgram(p1, 110);
    testProgram(p2, 110);
    testProgram(p3, 110);
  }
  
  @Test
  public void testNest() {
    Program p = new Program(new Function("main", new String[] {}, new Declaration[] {new Declaration("i"), new Declaration("j"), new Declaration("k")}, new Statement[] {
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
     }));

    testProgram(p, 3537);
  }
  
  @Test
  public void testFib() {
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
              new Return(new Binary(new Variable("first"), Binop.Plus, new Call("crazyFib", new Binary(new Variable("n"), Binop.Minus, new Number(2)))))
          });
      Function main = new Function("main", new String[] {}, new Declaration[] {},
          new Statement[] {new Return(new Call("crazyFib", new Number(n)))});
      return new Program(main, crazyFib);
    };

    testProgram(getFibProgram.apply(0), 0);
    testProgram(getFibProgram.apply(1), 1);
    testProgram(getFibProgram.apply(4), 3);
    testProgram(getFibProgram.apply(10), 55);
    testProgram(getFibProgram.apply(30), 832040);
  }
  
  @Test
  public void testCompare() {
    TriFunction<Integer, Comp, Integer, Program> getProgram = (a, comp, b) -> {
      Function main = new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
          new IfThenElse(new Comparison(new Number(a), comp, new Number(b)),
              new Return(new Number(1)),
              new Return(new Number(0))) 
       });
      return new Program(main);
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

  /*
  @Test
  public void testExprStmt() {
    Function foo = new Function("foo", new String[] {}, new Declaration[] {}, new Statement[] {
       new Return(new Number(0))
    });
    Function main = new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
      new ExpressionStatement(new Number(42)),
      new ExpressionStatement(new Write(new Number(5))),
      new ExpressionStatement(new Call("foo")),
      new Return(new Number(0))
    });
    Program p = new Program(main, foo);
    
    testProgram(p, 0);
    
    fail("Prüfen, dass 5 ausgegeben wurde");
  }
  */
  
  @Test
  public void testDoWhile() {
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
      return new Program(main);
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
              new Return(new Call("ggt", new Number(a), new Number(b)))});
      return new Program(ggt, mainFunctionGgt);
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
                  new Binary(new Variable("n"), Binop.Minus, new Number(1)))));
      Function fakFunc = new Function("fak", new String[] {"n"}, new Declaration[] {},
          new Statement[] {fakRecEnd, fakRec});
      Function mainFunctionFak = new Function("main", new String[] {}, new Declaration[] {},
          new Statement[] {new Return(new Call("fak", new Number(n)))});
      return new Program(mainFunctionFak, fakFunc);
    };
    
    testProgram(getFakProgram.apply(3), 6);
    testProgram(getFakProgram.apply(10), 3628800);
  }
}
