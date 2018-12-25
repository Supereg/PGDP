package de.andi.minijava.assembler.instructions;

import de.andi.minijava.assembler.AsmVisitor;
import de.andi.minijava.assembler.operations.CompareOperation;

public class Cmp extends Instruction {

    private CompareOperation operator;

    public Cmp(CompareOperation operator) {
        this.operator = operator;
    }

    public CompareOperation getOperator() {
        return operator;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}