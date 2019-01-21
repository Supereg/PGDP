package asm;

public class Ldi extends Instruction { // loads constant to stack

    private int immediate;

    public Ldi(int immediate) {
        this.immediate = immediate;
    }

    public int getImmediate() {
        return immediate;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}