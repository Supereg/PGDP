package de.andi.minijava.assembler.instructions;

import de.andi.minijava.assembler.AsmVisitor;

public class Brc extends Instruction {

    private int programAddress;

    public Brc(int programAddress) {
        this.programAddress = programAddress;
    }

    public int getProgramAddress() {
        return programAddress;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}