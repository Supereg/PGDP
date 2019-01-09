package de.andi.minijava.assembler.instructions;

import de.andi.minijava.assembler.AsmVisitor;

public class Or extends Instruction {

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}