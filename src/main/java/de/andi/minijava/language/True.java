package de.andi.minijava.language;

public class True extends Condition {

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}