package de.andi.minijava.assembler.instructions;

import de.andi.minijava.assembler.AsmVisitor;

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