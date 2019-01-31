package asm;

import asm.exceptions.*;
import codegen.Terminal;

import java.util.*;
import java.util.Map.Entry;

public class Interpreter implements AsmVisitor {

    private static final int K = 5;

    private final Instruction[] instructions;

    private int nextThreadId = 0;
    private InterpreterThread currentThread;

    private List<InterpreterThread> runningThreads = new ArrayList<>();
    private Map<InterpreterThread, Integer> joiningThreads = new LinkedHashMap<>(); // LinkedHashMap to have fair waiting
    private Map<InterpreterThread, Integer> blockedThreads = new LinkedHashMap<>();

    private Map<Integer, Integer> terminatedThreads = new HashMap<>();

    private final int[] heap = new int[1024];
    private int heapPointer = 0; // next free cell
    private Map<Integer, Integer> lockedHeapAddresses = new HashMap<>(); // Map<Heap address, ID of thread which acquired the lock>

    private boolean halt;

    /**
     * Instantiates Interpreter object
     *
     * @param instructions  instructions to run
     * @throws IllegalArgumentException if the passed {@code instructions} are {@code null} or empty
     */
    public Interpreter(Instruction[] instructions) {
        if (instructions == null || instructions.length == 0)
            throw new IllegalArgumentException("instructions must not be null or empty");
        this.instructions = instructions;

        InterpreterThread mainThread = new InterpreterThread(nextThreadId++); // main thread
        running(mainThread);
    }

    public int execute() {
        int threadSelect = 0;

        execution: while (true) {
            if (runningThreads.size() == 0)
                throw new DeadLockException();

            // change thread, simply going to the next index (our list ist ordered by thread ID)
            threadSelect = (threadSelect + 1) % runningThreads.size();
            currentThread = runningThreads.get(threadSelect);

            for (int k = K; k > 0; k--) {
                try {
                    Instruction instruction = instructions[currentThread.getProgramCounter()];
                    currentThread.incrementProgramCounter();

                    try {
                        instruction.accept(this);
                    } catch (InterpreterException e) {
                        throw e; // needs to be rethrown (such has HaltException)
                    } catch (Exception e) {
                        throw new KernelPanicException(e);
                    }

                    currentThread.tick();

                    if (halt) {
                        // when the program has nothing to return, it will exit with StackUnderflowException
                        int returnValue = popValueFromStack();
                        terminate(currentThread, returnValue);

                        if (currentThread.getId() != 0)
                            throw new IllegalHaltInstructionException();

                        if (runningThreads.size() != 0 || blockedThreads.size() != 0 || joiningThreads.size() != 0)
                            throw new ThreadsStillRunningException();

                        if (lockedHeapAddresses.size() != 0)
                            throw new LocksStillAcquiredException();
                        break execution;
                    }

                    if (!currentThread.isRunning()) // current thread terminated, is joining or blocked
                        break;
                } catch (InterpreterException e) {
                    // System.err.println("Error on line " + (currentThread.getProgramCounter() - 1)); // debug
                    throw e; // needs to be rethrown otherwise catch(Exception e) would handle it
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalProgramCounterException("End of program stream, but program didn't halt itself");
                } catch (Exception e) {
                    throw new KernelPanicException(e);
                }
            }
        }

        return terminatedThreads.get(0); // return value from thread 0 (main)
    }

    private void running(InterpreterThread thread) {
        int position = Collections.binarySearch(runningThreads, thread); // if it is contained ... otherwise, <code>(-(<i>insertion point</i>) - 1)</code>
        if (position >= 0)
            throw new RuntimeException("Tried adding thread again to running threads"); // debugging; should not happen

        // we insert the thread in an ordered way, so thread changes get easier
        runningThreads.add(- position - 1, thread);
        thread.setRunning(true);
    }

    private void joining(InterpreterThread thread, int threadId) {
        runningThreads.remove(thread);
        thread.setRunning(false);

        joiningThreads.put(thread, threadId);
    }

    private void terminate(InterpreterThread thread, int returnValue) {
        runningThreads.remove(thread);
        thread.setRunning(false);

        terminatedThreads.put(thread.getId(), returnValue);

//        if (thread.getStackPointer() > 0) // debug stuff
//            System.err.println("WARNING! More than one element on stack after termination on stack " + thread.getId() + "!");

        List<InterpreterThread> remove = new ArrayList<>();
        for (Entry<InterpreterThread, Integer> entry: joiningThreads.entrySet()) {
            if (entry.getValue() != thread.getId())
                continue;

            entry.getKey().pushValueToStack(returnValue); // push return value to the joining thread
            remove.add(entry.getKey());
        }

        for (InterpreterThread thread0: remove) {
            joiningThreads.remove(thread0);
            running(thread0); // set joining thread running again
        }
    }

    private int popValueFromStack() {
        return currentThread.popValueFromStack();
    }

    private void pushValueToStack(int element) {
        currentThread.pushValueToStack(element);
    }
    @Override
    public void visit(Nop nop) {}

    @Override
    public void visit(Add add) {
        int value = popValueFromStack() + popValueFromStack();
        pushValueToStack(value);
    }

    @Override
    public void visit(Sub sub) {
        int value = popValueFromStack() - popValueFromStack();
        pushValueToStack(value);
    }

    @Override
    public void visit(Mul mul) {
        int value = popValueFromStack() * popValueFromStack();
        pushValueToStack(value);
    }

    @Override
    public void visit(Mod mod) {
        int value;
        try {
            value = popValueFromStack() % popValueFromStack();
        } catch (java.lang.ArithmeticException e) {
            throw new asm.exceptions.ArithmeticException(e.getMessage());
        }
        pushValueToStack(value);
    }

    @Override
    public void visit(Div div) {
        int value;
        try {
            value = popValueFromStack() / popValueFromStack();
        } catch (java.lang.ArithmeticException e) {
            throw new asm.exceptions.ArithmeticException(e.getMessage());
        }

        pushValueToStack(value);
    }

    @Override
    public void visit(And and) {
        int value = popValueFromStack() & popValueFromStack();
        pushValueToStack(value);
    }

    @Override
    public void visit(Or or) {
        int value = popValueFromStack() | popValueFromStack();
        pushValueToStack(value);
    }

    @Override
    public void visit(Not not) {
        int value = ~popValueFromStack();
        pushValueToStack(value);
    }

    @Override
    public void visit(Ldi ldi) {
        pushValueToStack(ldi.getImmediate());
    }

    @Override
    public void visit(Lfs lfs) { // load from stack
        int value = currentThread.loadLocalVariable(lfs.getVariable());
        pushValueToStack(value);
    }

    @Override
    public void visit(Sts sts) { // store to stack
        int value = popValueFromStack();
        currentThread.saveLocalVariable(sts.getVariable(), value);
    }

    @Override
    public void visit(Brc brc) {
        if (brc.getProgramAddress() < 0 || brc.getProgramAddress() >= instructions.length)
            throw new InvalidJumpTargetException();

        if (popValueFromStack() == -1)
            currentThread.jump(brc.getProgramAddress());
    }

    @Override
    public void visit(Cmp cmp) {
        int o1 = popValueFromStack();
        int o2 = popValueFromStack();

        boolean evaluation;
        switch (cmp.getOperator()) {
            case LT:
                evaluation = o1 < o2;
                break;
            case EQ:
                evaluation = o1 == o2;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        pushValueToStack(evaluation? -1: 0);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void visit(Call call) {
        int method = popValueFromStack();
        if (method < 0 || method >= instructions.length)
            throw new InvalidMethodAddressException();

        int[] arguments = new int[call.getArgumentCount()]; // pop arguments
        for (int i = call.getArgumentCount() - 1; i >= 0; i--) {
            int argument = popValueFromStack();
            arguments[i] = argument;
        }

        pushValueToStack(currentThread.getFramePointer());
        pushValueToStack(currentThread.getProgramCounter()); // pushing the address of the next instruction

        for (int argument : arguments)
            pushValueToStack(argument);

        currentThread.jump(method);
        currentThread.setFramePointer(currentThread.getStackPointer());

        currentThread.allowDecl();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void visit(Fork fork) {
        int method = popValueFromStack();
        if (method < 0 || method >= instructions.length)
            throw new InvalidMethodAddressException();
        
        int[] arguments = new int[fork.getArgumentCount()]; // pop arguments
        for (int i = fork.getArgumentCount() - 1; i >= 0; i--) {
            int argument = popValueFromStack();
            arguments[i] = argument;
        }

        InterpreterThread forkedThread = new InterpreterThread(nextThreadId++);
        forkedThread.pushValueToStack(-1); // framePointer
        forkedThread.pushValueToStack(-1); // programmCounter

        for (int argument : arguments)
            forkedThread.pushValueToStack(argument);

        forkedThread.jump(method);
        forkedThread.setFramePointer(forkedThread.getStackPointer());

        forkedThread.allowDecl();

        running(forkedThread);

        pushValueToStack(forkedThread.getId());
    }

    @Override
    public void visit(Decl decl) {
        currentThread.declareVariables(decl.getVariableAmount());
        currentThread.allowDecl();
    }

    @Override
    public void visit(Return returnInstruction) {
        if (returnInstruction.getVariableAndArgumentCount() < 0)
            throw new InvalidStackFrameSizeException();

        int returnValue = popValueFromStack();

        for (int i = 0; i < returnInstruction.getVariableAndArgumentCount(); i++) // empty stack
            popValueFromStack();

        int programmCounter = popValueFromStack();
        if (programmCounter == -1) {
            popValueFromStack(); // clear stack (framePointer)
            terminate(currentThread, returnValue);
            return;
        }

        if (programmCounter < 0 || programmCounter >= instructions.length)
            throw new InvalidReturnAddressException();
        currentThread.jump(programmCounter);

        currentThread.setFramePointer(popValueFromStack()); // error handling is in #setFramePointer

        pushValueToStack(returnValue);
    }

    @Override
    public void visit(In in) {
        int input = Terminal.askInt("IN > ");
        pushValueToStack(input);
    }

    @Override
    public void visit(Out out) {
        System.out.println(popValueFromStack());
    }

    @Override
    public void visit(Push push) {
        // illegal register numbers are handled in Push constructor
        pushValueToStack(currentThread.r(push.getRegister()));
    }

    @Override
    public void visit(Pop pop) {
        int value = popValueFromStack();
        // illegal register numbers are handled in Pop constructor
        currentThread.r(pop.getRegister(), value);
    }

    @Override
    public void visit(Halt halt) {
        this.halt = true;
    }

    @Override
    public void visit(Alloc alloc) {
        int size = popValueFromStack();

        if (size < 0)
            throw new NegativeMemoryAllocationException();
        if (heap.length - (heapPointer + size + 1) <= 0)
            throw new OutOfMemoryException();

        heap[heapPointer++] = size; // save the size one BEFORE the array address

        int address = heapPointer;
        heapPointer += size;

        pushValueToStack(address);
    }

    @Override
    public void visit(Lfh lfh) {
        int address = popValueFromStack();
        if (address < 0 || address >= heapPointer)
            throw new InvalidHeapAccessException();

        int value = heap[address];
        pushValueToStack(value);
    }

    @Override
    public void visit(Sth sth) {
        int address = popValueFromStack();
        if (address < 0 || address >= heapPointer)
            throw new InvalidHeapAccessException();

        int value = popValueFromStack();

        heap[address] = value;
    }

    @Override
    public void visit(Join join) {
        int threadId = popValueFromStack();

        if (threadId >= nextThreadId)
            throw new IllegalThreadIdException();

        if (terminatedThreads.containsKey(threadId))
            pushValueToStack(terminatedThreads.get(threadId));
        else
            joining(currentThread, threadId);
    }

    @Override
    public void visit(Lock lock) {
        int heapAddress = popValueFromStack();

        Integer currentLock = lockedHeapAddresses.get(heapAddress);

        if (currentLock == null)
            lockedHeapAddresses.put(heapAddress, currentThread.getId());
        else if (currentLock != currentThread.getId()) {
            runningThreads.remove(currentThread);
            currentThread.setRunning(false);
            blockedThreads.put(currentThread, heapAddress);
        }
        // otherwise lock is already acquired by current thread -> we do nothing (doesn't hurt anyone, and also expected behaviour in Java)
    }

    @Override
    public void visit(Unlock unlock) {
        int heapAddress = popValueFromStack();

        Integer currentLock = lockedHeapAddresses.get(heapAddress);

        if (currentLock == null || currentLock != currentThread.getId())
            throw new CannotUnlockUnlockedLockException();
        else {
            lockedHeapAddresses.remove(heapAddress);

            for (Entry<InterpreterThread, Integer> entry: blockedThreads.entrySet()) {
                if (entry.getValue() == heapAddress) {
                    InterpreterThread blockedThread = entry.getKey();

                    blockedThreads.remove(blockedThread); // since we break afterwards we can modify blockedThreads while being in loop
                    running(blockedThread); // set running again
                    lockedHeapAddresses.put(heapAddress, blockedThread.getId()); // acquire lock
                    break;
                }
            }
        }
    }

}