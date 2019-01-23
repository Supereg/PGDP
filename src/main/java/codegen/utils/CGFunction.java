package codegen.utils;

import codegen.Parameter;
import codegen.Type;
import codegen.exceptions.VariableNotFoundException;

import java.util.*;

public class CGFunction {

    private final String name;
    private final Type returnType;

    private final List<Parameter> parameters;
    private final List<CGDeclaration> declarations;

    private Set<String> variables;

    private int instructionIndex; // index only set after whole program is assembled
    private boolean instructionIndexSet;

    public CGFunction(String name, Type returnType, Parameter[] parameters) {
        this.name = name;
        this.returnType = returnType;

        this.parameters = new ArrayList<>(parameters.length);
        Collections.addAll(this.parameters, parameters);
        this.variables = new HashSet<>(parameters.length);

        this.declarations = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public boolean isMain() {
        return name.equals("main");
    }

    public int getParameterSize() {
        return parameters.size();
    }

    public boolean addParameter(Parameter parameter) {
        return variables.add(parameter.getName());
    }

    public boolean addDeclaration(String name) {
        CGDeclaration declaration = new CGDeclaration(name);

        if (declarations.contains(declaration))
            return false;

        declarations.add(declaration);
        return variables.add(name);
    }

    /**
     * Returns the num to use for LFS and STS instructions
     *
     * @param name  name of the variable
     * @return      returns the num to use for LFS and STS instructions
     * @throws VariableNotFoundException    if variable couldn't be found
     */
    public int selectVariable(String name) throws VariableNotFoundException {
        int declIndex = declarations.indexOf(new CGDeclaration(name));

        if (declIndex >= 0)
            return declIndex + 1; // local variables begin at 1
        else {
            int parameterIndex = parameters.indexOf(new Parameter(Type.Int, name));

            if (parameterIndex < 0)
                throw new VariableNotFoundException(name);
            /*
            Looking at the example of main(int a, int b)
            parameters looks the following {"a", "b"}

            however variable index for a: -1 and b: 0
            so we need to translate this here
             */
            final int minIndex = -(parameters.size() - 1); // this is the littlest variable index possible
            return minIndex + parameterIndex;
        }
    }

    private CGDeclaration getDeclaration(int index) {
        return declarations.get(index);
    }

    public boolean isDeclarationWritten(int variableNum) {
        if (variableNum <= 0) // variableNum refers to an parameter
            return true;

        variableNum--; // realign variableNum

        CGDeclaration declaration = getDeclaration(variableNum);
        return declaration.isWritten();
    }

    public void setDeclarationWritten(int variableNum) {
        if (variableNum <= 0) // variableNum refers to an parameter
            return;

        variableNum--; // realign variableNum

        CGDeclaration declaration = getDeclaration(variableNum);
        declaration.setWritten(true);
    }

    public int getDeclarationSize() {
        return declarations.size();
    }

    public int getInstructionIndex() {
        if (!instructionIndexSet)
            throw new IllegalStateException("instructionIndex of " + name + " is not yet set!"); // debug stuff

        return instructionIndex;
    }

    public void setInstructionIndex(int instructionIndex) {
        this.instructionIndex = instructionIndex;
        this.instructionIndexSet = true;
    }

    @Override
    public boolean equals(Object o) { // is the parameterSize relevant?
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CGFunction that = (CGFunction) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}