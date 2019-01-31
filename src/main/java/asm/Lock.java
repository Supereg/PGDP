package asm;

public class Lock extends Instruction {

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}