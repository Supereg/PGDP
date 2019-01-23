package codegen;

import asm.Instruction;
import asm.Interpreter;
import asm.exceptions.NegativeMemoryAllocationException;
import codegen.exceptions.FunctionNotFoundException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class CodeGenArraysTest {

    @Test
    public void testShort() {
        Program program = new Program(new Function("main", new Parameter[0], new Declaration[] {
                new Declaration(Type.IntArray, "array")
        }, new Statement[] {
                new Assignment("array", new ArrayAllocator(new Number(2))),
                new ArrayIndexAssignment(new Variable("array"), new Number(1), new Number(42)),
                new Return(new ArrayAccess(new Variable("array"), new Number(1)))
        }));

        assertEquals(42, runProgram(program));
    }

    @Test
    public void testExample() {
        java.util.function.Function<Integer, Program> createProgram = n -> {
            Function init = new Function(Type.IntArray, "init",
                    new Parameter[] { new Parameter(Type.Int, "size")},
                    new Declaration[] { new Declaration("i"), new Declaration(Type.IntArray, "array")},
                    new Statement[] {
                            new Assignment("i", new Number(0)),
                            new Assignment("array", new ArrayAllocator(new Variable("size"))),
                            new While(new Comparison(new Variable("i"), Comp.Less, new Variable("size")), new Composite(new Statement[] {
                                    new ArrayIndexAssignment(new Variable("array"), new Variable("i"), new Variable("i")),
                                    new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
                            }), false),
                            new Return(new Variable("array"))
                    });

            Function sum = new Function(Type.Int, "sum",
                    new Parameter[] { new Parameter(Type.IntArray, "array") },
                    new Declaration[] { new Declaration("i"), new Declaration("sum") },
                    new Statement[] {
                            new Assignment("i", new Number(0)),
                            new Assignment("sum", new Number(0)),
                            new While(new Comparison(new Variable("i"), Comp.Less, new ArrayLength(new Variable("array"))), new Composite(new Statement[] {
                                    new Assignment("sum", new Binary(new Variable("sum"), Binop.Plus, new ArrayAccess(new Variable("array"),
                                            new Variable("i")))),
                                    new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
                            }), false),
                            new Return(new Variable("sum"))
                    });

            Function main = new Function("main",
                    new String[] {},
                    new Declaration[] { new Declaration(Type.IntArray, "a") },
                    new Statement[] {
                            new Assignment("a", new Call("init", new Number(n))),
                            new Return(new Call("sum", new Variable("a")))
                    });

            return new Program(init, sum, main);
        };

        java.util.function.Function<Integer, Integer> calculateResult = n -> {
            int result = 0;
            for (int i = 0; i < n; i++) {
                result += i;
            }
            return result;
        };

        IntStream.of(0, 1, 2, 5, 7, 8, 10, 20, 30, 50).forEach(i ->
                assertEquals("Unexpected result for n=" + i, calculateResult.apply(i).intValue(), runProgram(createProgram.apply(i))));
    }

    @Test
    public void testZeroArraySize0() {
        Program program = new Program(new Function("main", new Parameter[0], new Declaration[] {
                new Declaration(Type.IntArray, "array")
        }, new Statement[] {
                new Assignment("array", new ArrayAllocator(new Number(0))),
                new Return(new ArrayLength(new Variable("array")))
        }));

        assertEquals(0, runProgram(program));
    }

    @Test
    public void testZeroArraySize1() {
        Program program = new Program(new Function("main", new Parameter[0], new Declaration[] {
                new Declaration(Type.IntArray, "array0"),
                new Declaration(Type.IntArray, "array1")
        }, new Statement[] {
                new Assignment("array0", new ArrayAllocator(new Number(0))),
                new Assignment("array1", new ArrayAllocator(new Number(42))),

                // accessing array0[0] should result in accessing array size of array1
                new Return(new ArrayAccess(new Variable("array0"), new Number(0)))
        }));

        assertEquals(42, runProgram(program));
    }

    @Test(expected = NegativeMemoryAllocationException.class)
    public void testNegativeArraySize() {
        Program program = new Program(new Function("main", new Parameter[0], new Declaration[] {
                new Declaration(Type.IntArray, "array")
        }, new Statement[] {
                new Assignment("array", new ArrayAllocator(new Number(-1))),
                new Return(new ArrayLength(new Variable("array")))
        }));

        assertEquals(0, runProgram(program));
    }

    @Test
    public void testMergeSort() {
        /*
        Testing the following MiniJava Program: recursive merge sort (taken from ParallelMergeSort)

        int main() {
            int pos;
            int[] array;
            pos = 0;
            array = new int[5];

            array[0] = 10; // dynamic
            array[1] = 5;
            array[2] = 61;
            array[3] = 1;
            array[4] = 3;

            sort(array, 0, 4);
            return array[pos];
        }

        int sort(int[] array, int low, int high) {
            int partitionSize, middle;

            partitionSize = (high - low) + 1;
            if (partitionSize == 1)
                return 0;

            middle = low + ((partitionSize -1) / 2);

            sort(array, low, middle);
            sort(array,middle + 1, high);
            mergeGroups(array, low, middle, high);
            return 0;
        }

        int mergeGroups(int[] array, int low, int middle, int high) {
            int[] sorted;
            int groupAPointer, groupBPointer, i;

            sorted = new int[(high - low) + 1];

            groupAPointer = low;
            groupBPointer = middle + 1;

            i = 0;
            while (i < length(array)) {
                if (groupAPointer > middle) {
                    sorted[i] = array[groupBPointer];
                    groupBPointer = groupBPointer + 1;
                }
                else {
                    if (groupBPointer > high) {
                        sorted[i] = array[groupAPointer];
                        groupAPointer = groupAPointer + 1;
                    }
                    else  {
                        if (array[groupAPointer] < array[groupBPointer]) {
                            sorted[i] = array[groupAPointer];
                            groupAPointer = groupAPointer + 1;
                        }
                        else {
                            sorted[i] = array[groupBPointer];
                            groupBPointer = groupBPointer + 1;
                        }
                    }
                }
                i = i + 1;
            }

            i = 0;
            while (i < length(sorted)) {
                array[low + i] = sorted[i];
                i = i + 1;
            }

            return 0;
        }
         */

        Function sort = new Function("sort", new Parameter[] {
                new Parameter(Type.IntArray, "array"),
                new Parameter(Type.Int, "low"),
                new Parameter(Type.Int, "high")
        }, new Declaration[] {
                new Declaration("partitionSize", "middle")
        }, new Statement[] {
                new Assignment("partitionSize", new Binary(new Binary(new Variable("high"), Binop.Minus, new Variable("low")), Binop.Plus, new Number(1))),
                new IfThen(new Comparison(new Variable("partitionSize"), Comp.Equals, new Number(1)),
                        new Return(new Number(0))),

                new Assignment("middle", new Binary(new Variable("low"), Binop.Plus,
                        new Binary(new Binary(new Variable("partitionSize"), Binop.Minus, new Number(1)), Binop.DivisionOperator, new Number(2)))),

                new ExpressionStatement(new Call("sort", new Variable("array"), new Variable("low"), new Variable("middle"))),
                new ExpressionStatement(new Call("sort", new Variable("array"), new Binary(new Variable("middle"), Binop.Plus, new Number(1)), new Variable("high"))),
                new ExpressionStatement(new Call("mergeGroups", new Variable("array"), new Variable("low"), new Variable("middle"), new Variable("high"))),
                new Return(new Number(0))
        });

        Function mergeGroups = new Function("mergeGroups", new Parameter[] {
                new Parameter(Type.IntArray, "array"),
                new Parameter(Type.Int, "low"),
                new Parameter(Type.Int, "middle"),
                new Parameter(Type.Int, "high")
        }, new Declaration[] {
                new Declaration(Type.IntArray, "sorted"),
                new Declaration(Type.Int, "groupAPointer", "groupBPointer", "i")
        }, new Statement[] {
                new Assignment("sorted",
                        new ArrayAllocator(new Binary(new Binary(new Variable("high"), Binop.Minus, new Variable("low")), Binop.Plus, new Number(1)))),

                new Assignment("groupAPointer", new Variable("low")),
                new Assignment("groupBPointer", new Binary(new Variable("middle"), Binop.Plus, new Number(1))),

                new Assignment("i", new Number(0)),
                new While(new Comparison(new Variable("i"), Comp.Less, new ArrayLength(new Variable("sorted"))), new Composite(new Statement[] {
                        new IfThenElse(new Comparison(new Variable("groupAPointer"), Comp.Greater, new Variable("middle")),
                                new Composite(new Statement[] {
                                        new ArrayIndexAssignment(new Variable("sorted"), new Variable("i"), new ArrayAccess(new Variable("array"), new Variable("groupBPointer"))),
                                        new Assignment("groupBPointer", new Binary(new Variable("groupBPointer"), Binop.Plus, new Number(1)))
                                }),
                                new Composite(new Statement[] {
                                        new IfThenElse(new Comparison(new Variable("groupBPointer"), Comp.Greater, new Variable("high")),
                                                new Composite(new Statement[] {
                                                        new ArrayIndexAssignment(new Variable("sorted"), new Variable("i"), new ArrayAccess(new Variable("array"), new Variable("groupAPointer"))),
                                                        new Assignment("groupAPointer", new Binary(new Variable("groupAPointer"), Binop.Plus, new Number(1)))
                                                }),
                                                new Composite(new Statement[] {
                                                        new IfThenElse(new Comparison(new ArrayAccess(new Variable("array"), new Variable("groupAPointer")), Comp.Less, new ArrayAccess(new Variable("array"), new Variable("groupBPointer"))),
                                                                new Composite(new Statement[] {
                                                                        new ArrayIndexAssignment(new Variable("sorted"), new Variable("i"), new ArrayAccess(new Variable("array"), new Variable("groupAPointer"))),
                                                                        new Assignment("groupAPointer", new Binary(new Variable("groupAPointer"), Binop.Plus, new Number(1)))
                                                                }),
                                                                new Composite(new Statement[] {
                                                                        new ArrayIndexAssignment(new Variable("sorted"), new Variable("i"), new ArrayAccess(new Variable("array"), new Variable("groupBPointer"))),
                                                                        new Assignment("groupBPointer", new Binary(new Variable("groupBPointer"), Binop.Plus, new Number(1)))
                                                                }
                                                                )),
                                                }
                                                )),
                                }
                                )),
                        new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
                }), false),

                new Assignment("i", new Number(0)),
                new While(new Comparison(new Variable("i"), Comp.Less, new ArrayLength(new Variable("sorted"))), new Composite(new Statement[] {
                        new ArrayIndexAssignment(
                                new Variable("array"),
                                new Binary(new Variable("low"), Binop.Plus, new Variable("i")),
                                new ArrayAccess(new Variable("sorted"), new Variable("i"))),
                        new Assignment("i", new Binary(new Variable("i"), Binop.Plus, new Number(1)))
                }), false),

                new Return(new Number(0))
        });

        Random random = new Random();
        int size = random.nextInt(19) + 1; // we do not want to sort empty arrays
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt();
        }

        List<Statement> statementList = new ArrayList<>();
        statementList.add(new Assignment("pos", new Number(0))); // will be replaced later
        statementList.add(new Assignment("array", new ArrayAllocator(new Number(size))));

        for (int i = 0; i < size; i++) {
            statementList.add(new ArrayIndexAssignment(new Variable("array"), new Number(i), new Number(array[i])));
        }

        statementList.add(new ExpressionStatement(new Call("sort", new Variable("array"), new Number(0), new Number(size -1))));
        statementList.add(new Return(new ArrayAccess(new Variable("array"), new Variable("pos"))));


        Arrays.sort(array);
        for (int i = 0; i < size; i++) {
            statementList.set(0, new Assignment("pos", new Number(i)));

            Function main = new Function("main", new String[0], new Declaration[] {
                    new Declaration("pos"),
                    new Declaration(Type.IntArray, "array")
            }, statementList.toArray(new Statement[0]));
            Program program = new Program(main, sort, mergeGroups);

            int result = runProgram(program);
            assertEquals("Unexpected mergerSort result for index " + i, array[i], result);
        }
    }

    @Test(expected = FunctionNotFoundException.class)
    public void testMainWithArrayReturnTyp() {
        Program program = new Program(new Function(Type.IntArray, "main", new Parameter[0], new Declaration[0], new Statement[] {new Return(new Number(0))}));
        runProgram(program);
    }

    @Test
    public void testRecursiveArray() {
        Function fun = new Function(Type.IntArray, "fun", new Parameter[] {new Parameter(Type.Int, "array0")}, new Declaration[] {
                new Declaration(Type.IntArray, "array1"),
                new Declaration(Type.Int, "i"),
        }, new Statement[] {
                new Assignment("array1", new ArrayAllocator(new Number(1))),
                new Assignment("i", new Binary(new ArrayAccess(new Variable("array0"), new Number(0)), Binop.Minus, new Number(1))),
                new ArrayIndexAssignment(new Variable("array1"), new Number(0), new Variable("i")),

                new IfThen(new Comparison(new Variable("i"), Comp.LessEqual, new Number(0)), new Return(new Variable("array1"))),
                new Return(new Call("fun", new Variable("array1")))
        });

        Function main = new Function(Type.Int, "main", new Parameter[0], new Declaration[] {new Declaration(Type.IntArray, "array")}, new Statement[] {
                new Assignment("array", new ArrayAllocator(new Number(1))),
                new ArrayIndexAssignment(new Variable("array"), new Number(0), new Number(20)),

                new Return(new ArrayAccess(new Call("fun", new Variable("array")), new Number(0)))
        });

        Program program = new Program(main, fun);

        assertEquals(0, runProgram(program));
    }

    private int runProgram(Program program) {
        CodeGenerationVisitor codeGeneration = new CodeGenerationVisitor(program);
        Instruction[] instructions = codeGeneration.getProgram();

        Interpreter interpreter = new Interpreter(instructions);
        return interpreter.execute();
    }

}