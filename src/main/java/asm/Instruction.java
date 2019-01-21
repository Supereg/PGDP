package asm;

public abstract class Instruction {

    public abstract void accept(AsmVisitor visitor);

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}