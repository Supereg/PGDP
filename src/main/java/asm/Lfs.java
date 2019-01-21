package asm;

public class Lfs extends Instruction {

    private int variable;

    public Lfs(int variable) {
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