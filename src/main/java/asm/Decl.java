package asm;

public class Decl extends Instruction {

    private int variableAmount;

    public Decl(int variableAmount) {
        this.variableAmount = variableAmount;
    }

    public int getVariableAmount() {
        return variableAmount;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}