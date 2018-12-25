package de.andi.minijava.language;

public class Read extends Expression {

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}