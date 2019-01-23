package codegen;

public class Declaration {

    private Type type;
    private String[] names;

    public Declaration(String... names) {
        this(Type.Int, names);
    }

    public Declaration(Type type, String... names) {
        this.type = type;
        this.names = names;

        if (names.length == 0)
            throw new IllegalArgumentException();
    }

    public Type getType() {
        return type;
    }

    public String[] getNames() {
        return names;
    }

    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}