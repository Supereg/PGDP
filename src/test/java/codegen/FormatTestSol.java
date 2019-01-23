package codegen;

import codegen.CodeGeneratorTestSol.TriFunction;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FormatTestSol {
  private static void printProgram(Program prog) {
    FormatVisitor fv = new FormatVisitor();
    prog.accept(fv);
    System.out.println(fv.getFormattedCode());
  }

  private static List<Method> getMethodsContaining(Class theClass, String match) {
    List<Method> results = new ArrayList<Method>();
    Method[] methods = theClass.getDeclaredMethods();

    for (Method m : methods) {
      if (m.getName().toLowerCase().contains(match)) {
        m.setAccessible(true);
        results.add(m);
      }
    }

    return results;
  }

  private static void testA() {
    System.out.println("+++++++++ testA");
    Function main = new Function("main",
        new String[] {},
        new Declaration[] {},
        new Statement[] {
          new ExpressionStatement(new Write(new Call("sum", new Read(), new Read()))),
          new Return(new Number(0))
    });
    
    Function sum = new Function("sum",
        new String[] {"a", "b"},
        new Declaration[] {},
        new Statement[] {
          new Return(new Binary(new Variable("a"), Binop.Plus, new Variable("b")))
    });
    
    printProgram(new Program(sum, main));
  }
  
  private static Program getGgtProgram(int a, int b) {
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
    Program ggtProgram = new Program(ggt, mainFunctionGgt);
    return ggtProgram;
  }

  private static Program getFakProgram(int n) {
    Statement fakRecEnd = new IfThen(new Comparison(new Variable("n"), Comp.Equals, new Number(0)),
        new Return(new Number(1)));
    Statement fakRec =
        new Return(new Binary(new Variable("n"), Binop.MultiplicationOperator, new Call("fak",
                new Binary(new Variable("n"), Binop.Minus, new Number(1)))));
    Function fakFunc = new Function("fak", new String[] {"n"}, new Declaration[] {},
        new Statement[] {fakRecEnd, fakRec});
    Function mainFunctionFak = new Function("main", new String[] {}, new Declaration[] {},
        new Statement[] {new Return(new Call("fak", new Number(n)))});
    Program fakProgram = new Program(mainFunctionFak, fakFunc);
    return fakProgram;
  }
  
  private static void testGGT() {
    System.out.println("+++++++++ testGGT");
    printProgram(getGgtProgram(12, 18));
  }

  private static void testFak() {
    System.out.println("+++++++++ testFak");
    printProgram(getFakProgram(3));
  }
  
  private static void testCompare() {
    System.out.println("+++++++++ testCompare");
    TriFunction<Integer, Comp, Integer, Program> getProgram = (a, comp, b) -> {
      Function main = new Function("main", new String[] {}, new Declaration[] {}, new Statement[] {
          new IfThenElse(new Comparison(new Number(a), comp, new Number(b)),
              new Return(new Number(1)),
              new Return(new Number(0))) 
       });
      return new Program(main);
    };
    
    printProgram(getProgram.apply(1, Comp.Equals, 10));
  }
  
  private static void testDoWhile() {
    System.out.println("+++++++++ testDoWhile");
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

    printProgram(getProgram.apply(true));
    printProgram(getProgram.apply(false));
  }


  private static void testVisitorCompliance() {
    System.out.println("+++++++++ testVisitorCompliance");

    String okayMsg = "OK - All classes fulfill rule";
    String rulePrefix = "+++ Rule ";

    // Rule 1: Count number of occurrences of methods named "accept" in each class
    System.out.println(rulePrefix + "'exactly 1 accept() method per class'");
    boolean rule1violated = false;

    for (Class cls : new Class[] {
      Program.class, Function.class, Declaration.class, Statement.class, Assignment.class, Composite.class,
      IfThen.class, IfThenElse.class, While.class, Return.class, ExpressionStatement.class, Expression.class,
      Variable.class, Number.class, Binary.class, Unary.class, Read.class, Write.class, Call.class,
      Condition.class, True.class, False.class, BinaryCondition.class, Comparison.class, UnaryCondition.class
    }) {
      List<Method> methods = getMethodsContaining(cls, "accept");

      if (methods.size() != 1) {
        System.out.println("ERR - Class " + cls.getName() + " violates rule; contains " + methods.size()
          + " accept() methods");
        rule1violated = true;
      }
    }

    if (!rule1violated)
      System.out.println(okayMsg);

    // Rule 2: Check the file contents for use of the "instanceof" operator and warn if it is found
    System.out.println(rulePrefix + "'no usage of instanceof to navigate the hierarchy'");
    boolean rule2violated = false;

    for (String name : new String[] {
      "Program", "Function", "Declaration", "Statement", "Assignment", "Composite", "IfThen", "IfThenElse", "While",
      "Return", "ExpressionStatement", "Expression", "Variable", "Number", "Binary", "Unary", "Read", "Write",
      "Call", "Condition", "True", "False", "BinaryCondition", "Comparison", "UnaryCondition"
    }) {
      String dirStr = "src/main/java/codegen";
      String pathStr = name + ".java";

      Path path = Paths.get(dirStr, pathStr);
      String fileReadWarn = "WARN - Could not read file " + path + "; check manually";

      if (! Files.isReadable(path)) {
        System.out.println(fileReadWarn);
        rule2violated = true;
        continue;
      }

      List<String> lines = null;

      try {
        lines = Files.readAllLines(path, StandardCharsets.UTF_8);
      } catch (Exception x) {
        System.out.println(fileReadWarn);
        rule2violated = true;
        continue;
      }

      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.contains("instanceof")) {
          System.out.println("WARN - Check file " + path + " manually! "
            + "It contains the instanceof operator in line "+ i + ":\n  >" + line);
          rule2violated = true;
        }
      }
    }

    if (!rule2violated)
      System.out.println(okayMsg);
  }

  private static void testSwitch() {
    System.out.println("+++++++++ testSwitch");
    TriFunction<Integer, Integer, Integer, Program> getSwitch = (x, y, k) -> {
    return new Program(new Function("main", new String[] {},
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
        }));
    };
    
    
    printProgram(getSwitch.apply(1, 2, 3));
  }


  @Test
  public void formatTest() {
    testA();
    testGGT();
    testFak();
    testCompare();
    testVisitorCompliance();
    testDoWhile();
    testSwitch();
  }

}
