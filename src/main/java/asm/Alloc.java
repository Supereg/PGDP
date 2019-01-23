package asm;

public class Alloc extends Instruction {

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}