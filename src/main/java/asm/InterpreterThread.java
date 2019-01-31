package asm;

import asm.exceptions.*;

public class InterpreterThread implements Comparable<InterpreterThread> {

    private final int id;

    private int[] stack = new int[128]; // default stack size
    private int stackPointer = -1;

    private int programCounter;
    private int framePointer = -1;

    private int[] registers = new int[2];

    private boolean declarationAllowed = true; // decl is allowed as first instruction
    private boolean declarationAllowedSet = false;

    private boolean running;

    public InterpreterThread(int id) {
        this.id = id;
    }

    public void tick() {
        if (!declarationAllowedSet)
            declarationAllowed = false;
        declarationAllowedSet = false;
    }

    public void allowDecl() {
        declarationAllowed = declarationAllowedSet = true;
    }

    public int getId() {
        return id;
    }

    public int getStackPointer() {
        return stackPointer;
    }

    public void declareVariables(int variableAmount) {
        if (!declarationAllowed)
            throw new IllegalDeclarationException();
        if (variableAmount < 0)
            throw new InvalidStackAllocationException();

        stackPointer += variableAmount;
        // Musterlösung does also not contain any error handling
    }

    public int loadLocalVariable(int variable) {
        int stackIndex = framePointer + variable;
        if (stackIndex < 0 || stackIndex >= stack.length)
            throw new IllegalVariableException("Tried loading local variable out of range"); // correspond to InvalidStackAccessException

        return stack[framePointer + variable];
    }

    public void saveLocalVariable(int variable, int value) {
        int stackIndex = framePointer + variable;
        if (stackIndex < 0 || stackIndex >= stack.length)
            throw new IllegalVariableException("Tried saving to local variable out of range");

        stack[framePointer + variable] = value;
    }

    public int popValueFromStack() {
        if (stackPointer < 0)
            throw new StackUnderflowException();

        return stack[stackPointer--];
    }

    public void pushValueToStack(int element) {
        if (++stackPointer >= stack.length)
            throw new StackOverflowException();
        stack[stackPointer] = element;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void jump(int programCounter) {
        this.programCounter = programCounter;
    }

    public void incrementProgramCounter() {
        programCounter++;
    }

    public int getFramePointer() {
        return framePointer;
    }

    public void setFramePointer(int pointer) {
        if (framePointer < -1 || framePointer >= stack.length)
            throw new InvalidFramePointerException();

        framePointer = pointer;
    }

    public int r(int index) {
        return registers[index];
    }

    public void r(int index, int value) {
        registers[index] = value;
    }

    @Override
    public int compareTo(InterpreterThread o) {
        return Integer.compare(id, o.id);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

}