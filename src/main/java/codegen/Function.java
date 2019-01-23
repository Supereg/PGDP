package codegen;

public class Function {

    private String name;
    private Type returnType;
    private Parameter[] parameters;

    private Declaration[] declarations;
    private Statement[] statements;

    public Function(String name, String[] parameters, Declaration[] declarations, Statement[] statements) {
        this(Type.Int, name, stringToParameterArray(parameters), declarations, statements);
    }

    public Function(String name, Parameter[] parameters, Declaration[] declarations, Statement[] statements) {
        this(Type.Int, name, parameters, declarations, statements);
    }

    public Function(Type returnType, String name, Parameter[] parameters, Declaration[] declarations, Statement[] statements) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.declarations = declarations;
        this.statements = statements;
    }

    public Function(Type returnType) {
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public Declaration[] getDeclarations() {
        return declarations;
    }

    public Statement[] getStatements() {
        return statements;
    }

    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

    private static Parameter[] stringToParameterArray(String[] parameters) {
        Parameter[] parametersArray = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parametersArray[i] = new Parameter(Type.Int, parameters[i]);
        }
        return parametersArray;
    }

}