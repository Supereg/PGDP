package de.andi.minijava.language;

public class Break extends Statement {

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}