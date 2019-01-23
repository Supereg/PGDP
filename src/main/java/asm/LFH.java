package asm;

public class LFH extends Instruction {

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}