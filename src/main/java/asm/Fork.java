package asm;

public class Fork extends Instruction {

    private int argumentCount;

    public Fork(int argumentCount) {
        this.argumentCount = argumentCount;
    }

    public int getArgumentCount() {
        return argumentCount;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}