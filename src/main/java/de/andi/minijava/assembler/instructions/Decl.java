package de.andi.minijava.assembler.instructions;

import de.andi.minijava.assembler.AsmVisitor;

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