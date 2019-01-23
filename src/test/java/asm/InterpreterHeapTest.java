package asm;

import asm.exceptions.InvalidHeapAccessException;
import asm.exceptions.NegativeMemoryAllocationException;
import asm.exceptions.OutOfMemoryException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InterpreterHeapTest {

    @Test
    public void testAlloc() {
        Instruction[] instructions0 = new Instruction[] {
                new Ldi(10),
                new Alloc(), // address is 1
                new Halt(),
        };

        Instruction[] instructions1 = new Instruction[] {
                new Ldi(10),
                new Alloc(), // address is 1
                new Ldi(5),
                new Alloc(), // address is 12
                new Add(),
                new Halt(),
        };

        assertEquals(1, run(instructions0));
        assertEquals(13, run(instructions1));
    }

    @Test(expected = NegativeMemoryAllocationException.class)
    public void testNegativAlloc() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(-1),
                new Alloc(),
                new Halt(),
        };

        run(instructions);
    }

    @Test(expected = OutOfMemoryException.class)
    public void testOutOfMemory() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(1022),
                new Alloc(),
                new Ldi(1),
                new Alloc(),// this alloc will overlap having enough space for 1 element but not for the size
                new Add(),
                new Halt(),
        };

        run(instructions);
    }

    @Test
    public void testLFH() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(1),
                new Ldi(123),
                new Alloc(),
                new Sub(),
                new LFH(),
                new Halt(),
        };

        assertEquals(123, run(instructions));
    }

    @Test(expected = InvalidHeapAccessException.class)
    public void testNegativLFH() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(2),
                new Alloc(),
                new Pop(0),
                new Ldi(-1),
                new LFH(),
                new Halt(),
        };

        run(instructions);
    }

    @Test(expected = InvalidHeapAccessException.class)
    public void testOutOfRangLFH0() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(0),
                new LFH(),
                new Halt(),
        };

        run(instructions);
    }

    @Test(expected = InvalidHeapAccessException.class)
    public void testOutOfRangLFH1() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(10),
                new Alloc(),
                new Pop(0),
                new Ldi(1025),
                new LFH(),
                new Halt(),
        };

        run(instructions);
    }

    @Test
    public void testSTH() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(22),
                new Alloc(),
                new Pop(0),
                new Ldi(23),
                new Ldi(1),
                new STH(),
                new Ldi(1),
                new LFH(),
                new Halt(),
        };

        assertEquals(23, run(instructions));
    }

    @Test(expected = InvalidHeapAccessException.class)
    public void testNegativSTH() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(2),
                new Alloc(),
                new Pop(0),
                new Ldi(22),
                new Ldi(-1),
                new STH(),
                new Halt(),
        };

        run(instructions);
    }

    @Test(expected = InvalidHeapAccessException.class)
    public void testOutOfRangSTH0() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(42),
                new Ldi(0),
                new STH(),
                new Halt(),
        };

        run(instructions);
    }

    @Test(expected = InvalidHeapAccessException.class)
    public void testOutOfRangSTH1() {
        Instruction[] instructions = new Instruction[] {
                new Ldi(10),
                new Alloc(),
                new Pop(0),
                new Ldi(42),
                new Ldi(1025),
                new STH(),
                new Halt(),
        };

        run(instructions);
    }

    private int run(Instruction[] instructions) {
        Interpreter interpreter = new Interpreter(instructions);
        return interpreter.execute();
    }

}