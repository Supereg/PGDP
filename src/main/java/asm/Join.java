package asm;

public class Join extends Instruction {

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}