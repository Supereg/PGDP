package asm;

public class Call extends Instruction {

    private int argumentCount;

    public Call(int argumentCount) {
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