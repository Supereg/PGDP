package codegen;

import org.junit.Ignore;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.fail;

@Ignore("FormatTestSol2 is a manual test")
public class FormatTestSol2 {
  /*
   * Insgesamt 2 Punkte auf die Formatierung.
   * 
   * Insbesondere auf folgende Dinge achten:
   *   - Rückgabewerte sind korrekt - init() z.B. gibt ein Array zurück
   *   - Array-Parameter sind korrekt Arrays
   *   - Array-Deklarationen sind Arrays
   */
  
  private static void printProgram(Program prog) {
    FormatVisitor fv = new FormatVisitor();
    prog.accept(fv);
    System.out.println(fv.getFormattedCode());
  }

  @Test
  public void testSimpleArray() {
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
    
    printProgram(p);
    fail("Ausgabe prüfen");
  }
  
  @Test
  public void testArrayExpr() {
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
    
    printProgram(p);
    fail("Ausgabe prüfen");
  }
  
  @Test
  public void testArrayLength() {
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
    
    printProgram(p);
    fail("Ausgabe prüfen");
  }
  
  @Test
  public void testMergesort() {
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
    
    printProgram(getProgram.apply(3));
    fail("Ausgabe prüfen");
  }
}
