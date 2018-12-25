package de.andi.minijava.language;

public abstract class Statement {

    public abstract void accept(ProgramVisitor visitor);

}