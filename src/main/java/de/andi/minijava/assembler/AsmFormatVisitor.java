package de.andi.minijava.assembler;

import de.andi.minijava.assembler.instructions.*;

public class AsmFormatVisitor implements AsmVisitor {

    private final Instruction[] instructions;

    private StringBuilder formatBuilder;

    public AsmFormatVisitor(Instruction[] instructions) {
        if (instructions == null || instructions.length == 0)
            throw new IllegalArgumentException("instructions must not be null or empty");
        this.instructions = instructions;
    }

    public String getFormattedCode() {
        if (formatBuilder == null)
            this.formatInstructions();

        return formatBuilder.toString();
    }

    private void formatInstructions() {
        this.formatBuilder = new StringBuilder();

        for (int i = 0; i < instructions.length; i++) {
            formatBuilder.append(i).append(": ");
            instructions[i].accept(this);

            if (i < instructions.length - 1)
                formatBuilder.append("\n");
        }
    }

    @Override
    public void visit(Nop nop) {
        formatBuilder.append("NOP");
    }

    @Override
    public void visit(Add add) {
        formatBuilder.append("ADD");
    }

    @Override
    public void visit(Sub sub) {
        formatBuilder.append("SUB");
    }

    @Override
    public void visit(Mul mul) {
        formatBuilder.append("MUL");
    }

    @Override
    public void visit(Mod mod) {
        formatBuilder.append("MOD");
    }

    @Override
    public void visit(Div div) {
        formatBuilder.append("DIV");
    }

    @Override
    public void visit(And and) {
        formatBuilder.append("AND");
    }

    @Override
    public void visit(Or or) {
        formatBuilder.append("OR");
    }

    @Override
    public void visit(Not not) {
        formatBuilder.append("NOT");
    }

    @Override
    public void visit(Ldi ldi) {
        formatBuilder.append("LDI ").append(ldi.getImmediate());
    }

    @Override
    public void visit(Lfs lfs) {
        formatBuilder.append("LFS ").append(lfs.getVariable());
    }

    @Override
    public void visit(Sts sts) {
        formatBuilder.append("STS ").append(sts.getVariable());
    }

    @Override
    public void visit(Brc brc) {
        formatBuilder.append("BRC ").append(brc.getProgramAddress());
    }

    @Override
    public void visit(Cmp cmp) {
        formatBuilder.append("CMP ").append(cmp.getOperator());
    }

    @Override
    public void visit(Call call) {
        formatBuilder.append("CALL ").append(call.getArgumentCount());
    }

    @Override
    public void visit(Decl decl) {
        formatBuilder.append("DECL ").append(decl.getVariableAmount());
    }

    @Override
    public void visit(Return returnInstruction) {
        formatBuilder.append("RETURN ").append(returnInstruction.getVariableAndArgumentCount());
    }

    @Override
    public void visit(In in) {
        formatBuilder.append("IN");
    }

    @Override
    public void visit(Out out) {
        formatBuilder.append("OUT");
    }

    @Override
    public void visit(Push push) {
        formatBuilder.append("PUSH ").append(push.getRegister());
    }

    @Override
    public void visit(Pop pop) {
        formatBuilder.append("POP ").append(pop.getRegister());
    }

    @Override
    public void visit(Halt halt) {
        formatBuilder.append("HALT");
    }

}