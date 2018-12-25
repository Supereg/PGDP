package de.andi.minijava.assembler.instructions;

import de.andi.minijava.assembler.AsmVisitor;

public abstract class Instruction {

    public abstract void accept(AsmVisitor visitor);

}