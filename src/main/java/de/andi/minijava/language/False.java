package de.andi.minijava.language;

public class False extends Condition {

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}