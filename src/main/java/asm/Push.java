package asm;

import asm.exceptions.IllegalRegisterException;

public class Push extends Instruction {

    private int register; // r0 or r1

    public Push(int register) {
        if (register!=0 && register!=1)
            throw new IllegalRegisterException("" + register);

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