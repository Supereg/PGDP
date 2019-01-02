package de.andi.minijava.language;

public class FormatVisitor implements ProgramVisitor {

    private String formattedCode;

    public String getFormattedCode() {
        return formattedCode;
    }

    @Override
    public void visit(Program program) {
        StringBuilder formatBuilder = new StringBuilder();

        Function[] functions = program.getFunctions();
        for (int i = 0; i < functions.length; i++) {
            functions[i].accept(this);

            formatBuilder.append(formattedCode);

            if (i < functions.length - 1)
                formatBuilder.append("\n\n");
        }

        formattedCode = formatBuilder.toString();
    }

    @Override
    public void visit(Function function) {
        StringBuilder formatBuilder = new StringBuilder("int ")
                .append(function.getName()).append("(");

        String[] parameters = function.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            formatBuilder.append("int ").append(parameters[i]);

            if (i < parameters.length - 1)
                formatBuilder.append(", ");
        }

        formatBuilder.append(") {");

        for (Declaration declaration : function.getDeclarations()) {
            declaration.accept(this);
            formatBuilder.append("\n  ").append(formattedCode);
        }

        for (Statement statement : function.getStatements()) {
            statement.accept(this);
            formatBuilder.append("\n  ").append(formattedCode);
        }

        formatBuilder.append("\n}"); // TODO empty ones above, how should it e printed

        formattedCode = formatBuilder.toString();
    }

    @Override
    public void visit(Declaration declaration) {
        StringBuilder formatBuilder = new StringBuilder("int ");

        String[] names = declaration.getNames();
        for (int i = 0; i < names.length; i++) {
            formatBuilder.append(names[i]);

            if (i < names.length - 1)
                formatBuilder.append(", ");
        }

        formatBuilder.append(";");

        formattedCode = formatBuilder.toString();
    }


    @Override
    public void visit(Assignment assignment) {
        assignment.getExpression().accept(this);
        formattedCode = assignment.getName() + " = " + formattedCode + ";";
    }

    @Override
    public void visit(Composite composite) {
        StringBuilder formatBuilder = new StringBuilder("{");

        for (Statement statement : composite.getStatements()) {
            statement.accept(this);
            formatBuilder.append("\n").append(formattedCode);
        }

        formatBuilder.append("\n}"); // TODO how to handle empty composite
        formattedCode = formatBuilder.toString();
    }

    @Override
    public void visit(IfThen ifThen) {
        ifThen.getCond().accept(this);
        StringBuilder formatBuilder = new StringBuilder("if (")
                .append(formattedCode).append(") {");

        if (ifThen.getThenBranch() != null) { // TODO allow empty bodies?
            ifThen.getThenBranch().accept(this);
            formatBuilder.append("\n").append("  ").append(formattedCode); // TODO einrücken
        }

        formatBuilder.append("\n}"); // TODO empty bodies syntax

        formattedCode = formatBuilder.toString();
    }

    @Override
    public void visit(IfThenElse ifThenElse) { // TODO subcalls from ifThen -> reuse method
        ifThenElse.getCond().accept(this);

        StringBuilder formatBuilder = new StringBuilder("if (")
                .append(formattedCode).append(") {");

        if (ifThenElse.getThenBranch() != null) { // TODO empty bodies
            ifThenElse.getThenBranch().accept(this);
            formatBuilder.append("\n").append("  ").append(formattedCode);
        }

        formatBuilder.append("\n}"); // TODO empty bodies

        formatBuilder.append("\nelse").append(" {");

        if (ifThenElse.getElseBranch() != null) {
            ifThenElse.getElseBranch().accept(this);
            formatBuilder.append("\n").append("  ").append(formattedCode);
        }

        formatBuilder.append("\n}");

        formattedCode = formatBuilder.toString();
    }

    @Override
    public void visit(While whileStatement) {
        // TODO brackets
    }

    @Override
    public void visit(Switch switchStatement) {
        // TODO switch
    }

    @Override
    public void visit(Return returnStatement) {
        returnStatement.getExpression().accept(this); // TODO NPE?
        formattedCode = "return " + formattedCode + ";";
    }

    @Override
    public void visit(Break breakStatement) {
        formattedCode = "break;";
    }

    @Override
    public void visit(ExpressionStatement expressionStatement) {
        expressionStatement.getExpression().accept(this);
        formattedCode += ";";
    }


    @Override
    public void visit(Variable variable) {
        formattedCode = variable.getName();
    }

    @Override
    public void visit(Number number) {
        formattedCode = number.getValue() + "";
    }

    @Override
    public void visit(Binary binary) {
        binary.getLhs().accept(this);
        String leftExpression = formattedCode;
        binary.getRhs().accept(this);
        String rightExpression = formattedCode;

        formattedCode = leftExpression + " " + binary.getOperator() + " " + rightExpression;
    }

    @Override
    public void visit(Unary unary) {
        unary.getOperand().accept(this);
        formattedCode = unary.getOperator() + formattedCode;
    }

    @Override
    public void visit(Read read) {
        formattedCode = "read()";
    }

    @Override
    public void visit(Write write) {
        write.getExpression().accept(this);
        formattedCode = "write(" + formattedCode + ")";
    }

    @Override
    public void visit(Call call) {
        StringBuilder callBuilder = new StringBuilder(call.getFunctionName()).append("(");

        Expression[] arguments = call.getArguments();
        for (int i = 0; i < arguments.length; i++) {
            arguments[i].accept(this);

            callBuilder.append(formattedCode);

            if (i < arguments.length - 1)
                callBuilder.append(", ");
        }

        formattedCode = callBuilder.append(")").toString();
    }


    @Override
    public void visit(True trueCondition) {
        formattedCode = "true";
    }

    @Override
    public void visit(False falseCondition) {
        formattedCode = "false";
    }

    @Override
    public void visit(BinaryCondition binaryCondition) {
        binaryCondition.getLhs().accept(this);
        String leftExpression = formattedCode;
        binaryCondition.getRhs().accept(this);
        String rightExpression = formattedCode;

        formattedCode = leftExpression + " " + binaryCondition.getOperator() + " " + rightExpression;
    }

    @Override
    public void visit(Comparison comparison) {
        comparison.getLhs().accept(this);
        String leftExpression = formattedCode;
        comparison.getRhs().accept(this);
        String rightExpression = formattedCode;

        formattedCode = leftExpression + " " + comparison.getOperator() + " " + rightExpression;
    }

    @Override
    public void visit(UnaryCondition unaryCondition) {
        unaryCondition.getOperand().accept(this);
        formattedCode = unaryCondition.getOperator() + formattedCode;
    }

}