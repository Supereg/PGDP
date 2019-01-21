package asm;

import asm.exceptions.*;
import codegen.Terminal;

public class Interpreter implements AsmVisitor {

    private final Instruction[] instructions;

    private final int[] stack = new int[128];

    private int r0;
    private int r1;

    private int stackPointer = -1;
    private int programCounter;
    private int framePointer = -1;

    private boolean declarationAllowed = true; // decl is allowed as first instruction
    private boolean declarationAllowedSet = false;

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
    }

    public int execute() {
        while (true) {
            try {
                Instruction instruction = instructions[programCounter++];
                try {
                    instruction.accept(this);
                } catch (InterpreterException e) {
                    throw e; // needs to be rethrown (such has HaltException)
                } catch (Exception e) {
                    throw new KernelPanicException(e);
                }

                if (!declarationAllowedSet)
                    declarationAllowed = false;

                declarationAllowedSet = false;
            } catch (HaltException e) {
                break;
            } catch (InterpreterException e) {
                throw e; // needs to be rethrown otherwise catch(Exception e) would handle it
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalProgramCounterException("End of program stream, but program didn't halt itself");
            } catch (Exception e) {
                throw new KernelPanicException(e);
            }
        }

        //if (stackPointer > 0)
        //    System.err.println("WARNING! More than one element on stack after termination!");

        return popValueFromStack(); // when the program has nothing to return, it will return with StackUnderflowException
    }

    private int popValueFromStack() {
        if (stackPointer < 0)
            throw new StackUnderflowException();

        return stack[stackPointer--];
    }

    private void pushValueToStack(int element) {
        if (++stackPointer >= stack.length)
            throw new StackOverflowException();
        stack[stackPointer] = element;
    }

    /*
    private void jump(int programAddress) {
        if (programAddress < 0 || programAddress >= stack.length)
            throw new IllegalProgramCounterException("" + programAddress);

        this.programCounter = programAddress;
    }
    */

    private void allowDecl() {
        declarationAllowed = declarationAllowedSet = true;
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
        int stackIndex = framePointer + lfs.getVariable();
        if (stackIndex < 0 || stackIndex >= stack.length)
            throw new IllegalVariableException("Tried loading local variable out of range"); // correspond to InvalidStackAccessException

        int value = stack[framePointer + lfs.getVariable()];
        pushValueToStack(value);
    }

    @Override
    public void visit(Sts sts) { // store to stack
        int stackIndex = framePointer + sts.getVariable();
        if (stackIndex < 0 || stackIndex >= stack.length)
            throw new IllegalVariableException("Tried saving to local variable out of range");

        int value = popValueFromStack();
        stack[framePointer + sts.getVariable()] = value;
    }

    @Override
    public void visit(Brc brc) {
        if (brc.getProgramAddress() < 0 || brc.getProgramAddress() >= instructions.length)
            throw new InvalidJumpTargetException();

        if (popValueFromStack() == -1)
            this.programCounter = brc.getProgramAddress();
    }

    @Override
    public void visit(Cmp cmp) {
        int o1 = popValueFromStack();
        int o2 = popValueFromStack();

        boolean evaluation;
        switch (cmp.getOperator()) {
            case LESS:
                evaluation = o1 < o2;
                break;
            case EQUALS:
                evaluation = o1 == o2;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        pushValueToStack(evaluation? -1: 0);
    }

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

        pushValueToStack(framePointer);
        pushValueToStack(programCounter); // pushing the address of the next instruction

        int i = 0;
        for (; i < arguments.length; i++) {
            int argument = arguments[i];
            pushValueToStack(argument);
        }

        this.programCounter = method;
        framePointer = stackPointer;

        allowDecl();
    }

    @Override
    public void visit(Decl decl) {
        if (!declarationAllowed)
            throw new IllegalDeclarationException();
        if (decl.getVariableAmount() < 0)
            throw new InvalidStackAllocationException();

        stackPointer += decl.getVariableAmount();
        allowDecl();
    }

    @Override
    public void visit(Return returnInstruction) {
        if (returnInstruction.getVariableAndArgumentCount() < 0)
            throw new InvalidStackFrameSizeException();

        int returnValue = popValueFromStack();

        for (int i = 0; i < returnInstruction.getVariableAndArgumentCount(); i++) // empty stack
            popValueFromStack();

        this.programCounter = popValueFromStack();
        if (programCounter < 0 || programCounter >= instructions.length)
            throw new InvalidReturnAddressException();

        framePointer = popValueFromStack();
        if (framePointer < -1 || framePointer >= stack.length)
            throw new InvalidFramePointerException();

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
        if (push.getRegister() == 0)
            pushValueToStack(r0);
        else if (push.getRegister() == 1)
            pushValueToStack(r1);
        // illegal register numbers are handled in Push constructor
    }

    @Override
    public void visit(Pop pop) {
        int value = popValueFromStack();
        if (pop.getRegister() == 0)
            r0 = value;
        else if (pop.getRegister() == 1)
            r1 = value;
        // illegal register numbers are handled in Pop constructor
    }

    @Override
    public void visit(Halt halt) {
        throw new HaltException();
    }

}