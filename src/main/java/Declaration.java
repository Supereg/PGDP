public class Declaration {

    private String[] names;

    public Declaration(String[] names) { // TODO names should not be empty
        this.names = names;
    }

    public String[] getNames() {
        return names;
    }

    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}