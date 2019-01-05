package de.andi.minijava.language;

public class Declaration {

    private String[] names;

    public Declaration(String[] names) {
        this.names = names;
    }

    public String[] getNames() {
        return names;
    }

    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}