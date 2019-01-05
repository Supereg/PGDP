package de.andi.minijava.language;

public class FormatVisitor implements ProgramVisitor {

    private StringBuilder formatBuilder = new StringBuilder();
    private int currentTabs;

    public FormatVisitor() {}

    public FormatVisitor(Program program) {
        program.accept(this);
    }

    public String getFormattedCode() {
        return formatBuilder.toString();
    }

    @Override
    public void visit(Program program) {
        currentTabs = 0;

        Function[] functions = program.getFunctions();
        for (int i = 0; i < functions.length; i++) {
            functions[i].accept(this);

            if (i < functions.length - 1)
                formatBuilder.append("\n\n");
        }
    }

    @Override
    public void visit(Function function) {
        formatBuilder.append("int ").append(function.getName()).append("(");

        String[] parameters = function.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            formatBuilder.append("int ").append(parameters[i]);

            if (i < parameters.length - 1)
                formatBuilder.append(", ");
        }

        formatBuilder.append(") {");

        currentTabs++;

        for (Declaration declaration : function.getDeclarations()) {
            formatBuilder.append("\n").append("  ");
            declaration.accept(this);
        }

        for (Statement statement : function.getStatements()) {
            formatBuilder.append("\n");
            statement.accept(this);
        }

        currentTabs--;

        formatBuilder.append("\n");
        appendTabs(); // wont append any tabs, however this is the correct way
        formatBuilder.append("}");
    }

    @Override
    public void visit(Declaration declaration) {
        // tabs are handled in #visit(function)
        formatBuilder.append("int ");

        String[] names = declaration.getNames();
        for (int i = 0; i < names.length; i++) {
            formatBuilder.append(names[i]);

            if (i < names.length - 1)
                formatBuilder.append(", ");
        }

        formatBuilder.append(";");
    }


    @Override
    public void visit(Assignment assignment) {
        appendTabs();

        formatBuilder.append(assignment.getName()).append(" = ");
        assignment.getExpression().accept(this);
        formatBuilder.append(";");
    }

    @Override
    public void visit(Composite composite) {
        formatBuilder.append("{"); // TODO when we do not get appended after control structure we would need to insert tabs

        currentTabs++;
        for (Statement statement: composite.getStatements()) {
            formatBuilder.append("\n");
            statement.accept(this);
        }
        currentTabs--;

        formatBuilder.append("\n");
        appendTabs();
        formatBuilder.append("}");
    }

    @Override
    public void visit(IfThen ifThen) {
        appendTabs();
        formatBuilder.append("if (");
        ifThen.getCond().accept(this);
        formatBuilder.append(")");

        Statement then = ifThen.getThenBranch();
        if (then == null)
            formatBuilder.append(";");
        else
            formatBody(then);
    }

    @Override
    public void visit(IfThenElse ifThenElse) {
        this.visit((IfThen) ifThenElse);

        if (ifThenElse.getThenBranch() instanceof Composite)
            formatBuilder.append(" else");
        else {
            formatBuilder.append("\n");
            appendTabs();
            formatBuilder.append("else");
        }

        Statement elseBranch = ifThenElse.getElseBranch();
        if (elseBranch == null)
            formatBuilder.append(";");
        else
            formatBody(elseBranch);
    }

    @Override
    public void visit(While whileStatement) {
        appendTabs();

        if (whileStatement.isDoWhile()) {
            formatBuilder.append("do");
            formatBody(whileStatement.getBody());

            if (whileStatement.getBody() instanceof Composite)
                formatBuilder.append(" while (");
            else {
                formatBuilder.append("\n");
                appendTabs();
                formatBuilder.append("while (");
            }

            whileStatement.getCondition().accept(this);
            formatBuilder.append(");");
        }
        else {
            formatBuilder.append("while (");
            whileStatement.getCondition().accept(this);
            formatBuilder.append(")");
            formatBody(whileStatement.getBody());
        }
    }

    @Override
    public void visit(Switch switchStatement) {
        appendTabs();
        formatBuilder.append("switch ("); // correct would be "switch ("
        switchStatement.getSwitchExpression().accept(this);
        formatBuilder.append(") {");

        currentTabs++;

        for (SwitchCase switchCase: switchStatement.getCases()) {
            formatBuilder.append("\n");
            appendTabs();
            formatBuilder.append("case ").append(switchCase.getNumber()).append(":");

            formatBody(switchCase.getCaseStatement());
        }

        if (switchStatement.getDefault() != null) {
            formatBuilder.append("\n");
            appendTabs();
            formatBuilder.append("default:");
            formatBody(switchStatement.getDefault());
        }

        currentTabs--;

        formatBuilder.append("\n");
        appendTabs();
        formatBuilder.append("}");
    }

    @Override
    public void visit(Return returnStatement) {
        appendTabs();
        formatBuilder.append("return ");
        returnStatement.getExpression().accept(this);
        formatBuilder.append(";");
    }

    @Override
    public void visit(Break breakStatement) {
        appendTabs();
        formatBuilder.append("break;");
    }

    @Override
    public void visit(ExpressionStatement expressionStatement) {
        appendTabs();
        expressionStatement.getExpression().accept(this);
        formatBuilder.append(";");
    }


    @Override
    public void visit(Variable variable) {
        formatBuilder.append(variable.getName());
    }

    @Override
    public void visit(Number number) {
        formatBuilder.append(number.getValue());
    }

    @Override
    public void visit(Binary binary) {
        bracketOperand(binary.getLhs());
        formatBuilder.append(" ").append(binary.getOperator()).append(" ");
        bracketOperand(binary.getRhs());
    }

    @Override
    public void visit(Unary unary) {
        formatBuilder.append(unary.getOperator());
        bracketOperand(unary.getOperand());
    }

    private void bracketOperand(Expression operand) {
        if (operand instanceof Binary) {
            // additionally we could check if we can leave out the brackets when same operations are used
            formatBuilder.append("(");
            operand.accept(this);
            formatBuilder.append(")");
        }
        else
            operand.accept(this);
    }

    @Override
    public void visit(Read read) {
        formatBuilder.append("read()");
    }

    @Override
    public void visit(Write write) {
        formatBuilder.append("write(");
        write.getExpression().accept(this);
        formatBuilder.append(")");
    }

    @Override
    public void visit(Call call) {
        formatBuilder.append(call.getFunctionName()).append("(");

        Expression[] arguments = call.getArguments();
        for (int i = 0; i < arguments.length; i++) {
            arguments[i].accept(this);

            if (i < arguments.length - 1)
                formatBuilder.append(", ");
        }

        formatBuilder.append(")");
    }


    @Override
    public void visit(True trueCondition) {
        formatBuilder.append("true");
    }

    @Override
    public void visit(False falseCondition) {
        formatBuilder.append("false");
    }

    @Override
    public void visit(BinaryCondition binaryCondition) {
        bracketCondition(binaryCondition.getLhs());
        formatBuilder.append(" ").append(binaryCondition.getOperator()).append(" ");
        bracketCondition(binaryCondition.getRhs());
    }

    @Override
    public void visit(Comparison comparison) {
        bracketOperand(comparison.getLhs());
        formatBuilder.append(" ").append(comparison.getOperator()).append(" ");
        bracketOperand(comparison.getRhs());
    }

    @Override
    public void visit(UnaryCondition unaryCondition) {
        formatBuilder.append(unaryCondition.getOperator());
        bracketCondition(unaryCondition.getOperand());
    }

    private void bracketCondition(Condition condition) {
        if (condition instanceof BinaryCondition || condition instanceof Comparison) {
            formatBuilder.append("(");
            condition.accept(this);
            formatBuilder.append(")");
        }
        else
            condition.accept(this);
    }

    private void formatBody(Statement statement) {
        if (statement instanceof Composite) {
            formatBuilder.append(" ");
            statement.accept(this);
        }
        else {
            formatBuilder.append("\n").append("  ");
            statement.accept(this);
        }
    }

    private void appendTabs() {
        for (int i = 0; i < currentTabs; i++) {
            formatBuilder.append("  ");
        }
    }

}