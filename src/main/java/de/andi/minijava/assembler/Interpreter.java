package de.andi.minijava.assembler;

import de.andi.minijava.Terminal;
import de.andi.minijava.assembler.exceptions.*;
import de.andi.minijava.assembler.exceptions.ArithmeticException;
import de.andi.minijava.assembler.instructions.*;

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

    public Interpreter(Instruction[] instructions) {
        this.instructions = instructions; // TODO ensure != null and not empty => collides with programCounter
    }

    public int execute() {
        while (true) {
            try {
                Instruction instruction = instructions[programCounter++];
                try {
                    instruction.accept(this);
                } catch (InterpreterException e) {
                    throw e; // needs to be rethrown (such has HaltException)
                } catch (java.lang.ArithmeticException e) { // TODO right thing to do?
                    throw new ArithmeticException(e.getMessage(), e);
                } catch (Exception e) {
                    throw new KernelPanicException(e);
                }

                if (!declarationAllowedSet)
                    declarationAllowed = false;

                declarationAllowedSet = false;
                // System.out.println("EXECUTING " + instruction.getClass().getSimpleName()); // TODO debug stuff
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

        if (stackPointer > 0)
            System.err.println("WARNING! More than one element on stack after termination!");

        return popValueFromStack(); // TODO what to return if the program has nothing done?
    }

    private int popValueFromStack() {
        if (stackPointer < 0)
            throw new StackEmptyException();

        return stack[stackPointer--];
    }

    private void pushValueToStack(int element) {
        if (++stackPointer >= stack.length)
            throw new StackOverflowException();
        stack[stackPointer] = element;
    }

    private void jump(int programAddress) {
        if (programAddress < 0 || programAddress >= stack.length)
            throw new IllegalProgramCounterException("" + programAddress);

        this.programCounter = programAddress;
    }

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
        int value = popValueFromStack() % popValueFromStack();
        pushValueToStack(value);
    }

    @Override
    public void visit(Div div) {
        int value = popValueFromStack() / popValueFromStack();
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
    public void visit(Lfs lfs) { // load from stack TODO test
        // TODO handle framePointer=-1 when lfs.getVariable == 0
        int value = stack[framePointer + lfs.getVariable()]; // TODO check for arrayIndexOutOfBounds
        pushValueToStack(value);
    }

    @Override
    public void visit(Sts sts) { // store to stack TODO test
        // TODO handle framePointer=-1
        int value = popValueFromStack();
        stack[framePointer + sts.getVariable()] = value; // TODO check for arrayIndexOutOfBounds
    }

    @Override
    public void visit(Brc brc) {
        if (popValueFromStack() == -1)
            jump(brc.getProgramAddress());
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

        int[] arguments = new int[call.getArgumentCount()]; // pop arguments
        for (int i = call.getArgumentCount() - 1; i >= 0; i--) {
            int argument = popValueFromStack();
            arguments[i] = argument;
        }

        pushValueToStack(programCounter); // pushing the address of the next instruction
        pushValueToStack(framePointer);

        int i = 0;
        for (; i < arguments.length; i++) {
            int argument = arguments[i];
            pushValueToStack(argument);
        }

        jump(method);
        framePointer = stackPointer;

        allowDecl();
    }

    @Override
    public void visit(Decl decl) {
        if (!declarationAllowed)
            throw new IllegalDeclarationException();

        stackPointer += decl.getVariableAmount();
        allowDecl();
    }

    @Override
    public void visit(Return returnInstruction) {
        int returnValue = popValueFromStack();

        for (int i = 0; i < returnInstruction.getVariableAndArgumentCount(); i++) // empty stack
            popValueFromStack();
        //stackPointer -= returnInstruction.getVariableAndArgumentCount(); // TODO validate

        framePointer = popValueFromStack();
        jump(popValueFromStack());

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