package de.andi.minijava.assembler.instructions;

import de.andi.minijava.assembler.AsmVisitor;
import de.andi.minijava.assembler.exceptions.IllegalRegisterException;

public class Pop extends Instruction {

    private int register;

    public Pop(int register) {
        if (register != 0 && register != 1)
            throw new IllegalRegisterException();

        this.register = register;
    }

    public int getRegister() {
        return register;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}