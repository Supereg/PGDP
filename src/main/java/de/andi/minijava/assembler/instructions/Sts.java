package de.andi.minijava.assembler.instructions;

import de.andi.minijava.assembler.AsmVisitor;

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