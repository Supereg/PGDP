package asm;

import asm.exceptions.CannotUnlockUnlockedLockException;
import asm.exceptions.DeadLockException;
import asm.exceptions.IllegalThreadIdException;
import asm.exceptions.LocksStillAcquiredException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InterpreterThreadsTest {

    @Test
    public void testRaceCondition() {
        Instruction[] instructions = {
                new Decl(1), // declare variable (for array address)
                new Ldi(1), // array size
                new Alloc(), // alloc array
                new Sts(1), // store array address to variable 1
                new Ldi(0), // init value for array (thread change)
                new Lfs(1), // load array address
                new Sth(), // initially set value

                new Lfs(1), // load argument (array heap address)
                new Ldi(23), // address of thread increment method
                new Fork(1), // call thread (thread change)
                new Pop(1), // save thread id to r1

                new Lfs(1), // load array address
                new Lfh(), // load array[0] value
                new Ldi(1),
                new Add(), // add one (thread change)
                new Lfs(1), // load heap address of array
                new Sth(), // storing increment back to array

                new Push(1), // load thread Id
                new Join(), // join on thread (isn't really necessary)
                new Pop(0), // ignore return value

                new Lfs(1), // load array address
                new Lfh(), // load return value (value of array)
                new Halt(), // Halt and return array value


                // increment executed in thread1
                new Lfs(0), // load array address
                new Lfh(), // load array[0] value
                new Ldi(1),
                new Add(), // add one
                new Lfs(0), // load heap address of array (thread change)
                new Sth(), // storing increment back to array

                new Ldi(0), // return value
                new Return(1)
        };

        assertEquals(1, run(instructions));
    }

    @Test
    public void testSynchronizedRaceCondition() {
        Instruction[] instructions = {
                new Decl(1), // declare variable (for array address)
                new Ldi(1), // array size
                new Alloc(), // alloc array
                new Sts(1), // store array address to variable 1
                new Ldi(0), // init value for array (thread change)
                new Lfs(1), // load array address
                new Sth(), // initially set value

                new Lfs(1), // load argument (array heap address)
                new Ldi(27), // address of thread increment method
                new Fork(1), // call thread (thread change)
                new Pop(1), // save thread id to r1

                new Lfs(1),
                new Lock(), // (thread change cause other thread locked array)

                new Lfs(1), // load array address
                new Lfh(), // load array[0] value
                new Ldi(1),
                new Add(), // add one
                new Lfs(1), // load heap address of array
                new Sth(), // storing increment back to array

                new Lfs(1),
                new Unlock(),

                new Push(1), // load thread Id
                new Join(), // join on thread (isn't really necessary)
                new Pop(0), // ignore return value

                new Lfs(1), // load array address
                new Lfh(), // load return value (value of array)
                new Halt(), // Halt and return array value


                // increment executed in thread1
                new Lfs(0), // load array address
                new Lock(), // lock on it

                new Lfs(0), // load array address
                new Lfh(), // load array[0] value
                new Ldi(1), // (thread change)
                new Add(), // add one
                new Lfs(0), // load heap address of array
                new Sth(), // storing increment back to array

                new Lfs(0),
                new Unlock(),

                new Ldi(0), // return value
                new Return(1)
        };

        assertEquals(2, run(instructions));
    }

    @Test(expected = DeadLockException.class)
    public void testDeadLock() {
        Instruction[] instructions = {
                new Ldi(4), // address
                new Fork(0),
                new Join(),
                new Halt(),
                new Ldi(0), // threadId 0
                new Join(),
                new Ldi(23),
                new Return(0)
        };

        run(instructions);
    }

    @Test(expected = CannotUnlockUnlockedLockException.class)
    public void testUnlockingUnlockedLock0() {
        Instruction[] instructions = {
                new Ldi(1),
                new Alloc(),
                new Unlock(),

                new Ldi(0),
                new Halt(),
        };

        run(instructions);
    }

    @Test(expected = CannotUnlockUnlockedLockException.class)
    public void testUnlockingUnlockedLock1() {
        Instruction[] instructions = {
                new Nop(),
                new Nop(),
                new Nop(),
                new Nop(),
                new Ldi(1),
                new Alloc(),
                new Pop(0),
                new Push(0),
                new Ldi(15),
                new Fork(1), // (thread change)
                new Pop(1),

                new Push(0),
                new Unlock(),
                new Ldi(0),
                new Halt(),

                new Lfs(0),
                new Lock(),
                new Nop(),
                new Nop(),
                new Nop(),
                new Lfs(0),
                new Unlock(),
                new Ldi(0),
                new Return(1),
        };

        run(instructions);
    }

    @Test(expected = IllegalThreadIdException.class)
    public void testIllegalThreadId() {
        Instruction[] instructions = {
                new Ldi(6),
                new Fork(0),
                new Ldi(1),
                new Add(),
                new Join(),
                new Halt(),

                new Ldi(0),
                new Return(0),
        };

        run(instructions);
    }

    @Test
    public void testJoinAfterThreadAlreadyTerminated() {
        Instruction[] instructions = {
                new Ldi(7),
                new Fork(0),
                new Nop(),
                new Nop(),
                new Nop(), // (thread change)
                new Join(),
                new Halt(),

                new Ldi(42),
                new Return(0),
        };

        assertEquals(42, run(instructions));
    }

    @Test(expected = LocksStillAcquiredException.class)
    public void testTerminationWithLockedLocks() {
        Instruction[] instructions = {
                new Ldi(1),
                new Alloc(),
                new Lock(),
                new Ldi(1),
                new Halt(),
        };

        run(instructions);
    }

    private int run(Instruction[] instructions) {
        Interpreter interpreter = new Interpreter(instructions);
        return interpreter.execute();
    }

}