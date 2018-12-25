package de.andi.minijava.language;

public abstract class Expression {

    public abstract void accept(ProgramVisitor visitor);

}