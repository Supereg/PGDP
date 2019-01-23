package asm;

public class STH extends Instruction {

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}