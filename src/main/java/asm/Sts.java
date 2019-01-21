package asm;

public class Sts extends Instruction {

    private int variable;

    public Sts(int variable) {
        this.variable = variable;
    }

    public int getVariable() {
        return variable;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}