package codegen;

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
        formatBuilder.append(function.getReturnType()).append(" ").append(function.getName()).append("(");

        Parameter[] parameters = function.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            formatBuilder.append(parameter.getType()).append(" ").append(parameter.getName());

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
        formatBuilder.append(declaration.getType()).append(" ");

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
        Statement[] statements = composite.getStatements();
        for (int i = 0; i < statements.length; i++) {
            statements[i].accept(this);

            if (i < statements.length - 1)
                formatBuilder.append("\n");
        }
    }

    @Override
    public void visit(IfThen ifThen) {
        appendTabs();
        formatBuilder.append("if (");
        ifThen.getCond().accept(this);
        formatBuilder.append(") {\n");

        currentTabs++;
        ifThen.getThenBranch().accept(this);
        currentTabs--;

        formatBuilder.append("\n");
        appendTabs();
        formatBuilder.append("}");
    }

    @Override
    public void visit(IfThenElse ifThenElse) {
        this.visit((IfThen) ifThenElse);

        formatBuilder.append(" else {\n");

        currentTabs++;
        ifThenElse.getElseBranch().accept(this);
        currentTabs--;

        formatBuilder.append("\n");
        appendTabs();
        formatBuilder.append("}");
    }

    @Override
    public void visit(While whileStatement) {
        appendTabs();
        if (whileStatement.isDoWhile())
            formatBuilder.append("do {\n");
        else {
            formatBuilder.append("while (");
            whileStatement.getCondition().accept(this);
            formatBuilder.append(") {\n");
        }

        currentTabs++;
        whileStatement.getBody().accept(this);
        currentTabs--;

        formatBuilder.append("\n");

        appendTabs();
        if (whileStatement.isDoWhile()) {
            formatBuilder.append("} while (");
            whileStatement.getCondition().accept(this);
            formatBuilder.append(");");
        }
        else
            formatBuilder.append("}");
    }

    @Override
    public void visit(Switch switchStatement) {
        appendTabs();
        formatBuilder.append("switch (");
        switchStatement.getSwitchExpression().accept(this);
        formatBuilder.append(") {");

        currentTabs++;

        for (SwitchCase switchCase: switchStatement.getCases()) {
            formatBuilder.append("\n");
            appendTabs();
            formatBuilder.append("case ")
                    .append(switchCase.getNumber().getValue())
                    .append(":\n");

            currentTabs++;
            switchCase.getCaseStatement().accept(this);
            currentTabs--;
        }

        if (switchStatement.getDefault() != null) {
            formatBuilder.append("\n");
            appendTabs();
            formatBuilder.append("default:\n");

            currentTabs++;
            switchStatement.getDefault().accept(this);
            currentTabs--;
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

    @SuppressWarnings("Duplicates")
    @Override
    public void visit(Binary binary) {
        formatBuilder.append("(");
        binary.getLhs().accept(this);
        formatBuilder.append(" ").append(binary.getOperator()).append(" ");
        binary.getRhs().accept(this);
        formatBuilder.append(")");
    }

    @Override
    public void visit(Unary unary) {
        formatBuilder.append(unary.getOperator());
        unary.getOperand().accept(this);
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
        if (call.isFork())
            formatBuilder.append("fork:");

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

    @SuppressWarnings("Duplicates")
    @Override
    public void visit(BinaryCondition binaryCondition) {
        formatBuilder.append("(");
        binaryCondition.getLhs().accept(this);
        formatBuilder.append(" ").append(binaryCondition.getOperator()).append(" ");
        binaryCondition.getRhs().accept(this);
        formatBuilder.append(")");
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void visit(Comparison comparison) {
        formatBuilder.append("(");
        comparison.getLhs().accept(this);
        formatBuilder.append(" ").append(comparison.getOperator()).append(" ");
        comparison.getRhs().accept(this);
        formatBuilder.append(")");
    }

    @Override
    public void visit(UnaryCondition unaryCondition) {
        formatBuilder.append(unaryCondition.getOperator());
        unaryCondition.getOperand().accept(this);
    }

    @Override
    public void visit(ArrayAllocator arrayAllocator) {
        formatBuilder.append("new int[");
        arrayAllocator.getSize().accept(this);
        formatBuilder.append("]");
    }

    @Override
    public void visit(ArrayAccess arrayAccess) {
        arrayAccess.getArray().accept(this);
        formatBuilder.append("[");
        arrayAccess.getIndex().accept(this);
        formatBuilder.append("]");
    }

    @Override
    public void visit(ArrayIndexAssignment arrayIndexAssignment) {
        appendTabs();
        arrayIndexAssignment.getArray().accept(this);
        formatBuilder.append("[");
        arrayIndexAssignment.getIndex().accept(this);
        formatBuilder.append("] = ");
        arrayIndexAssignment.getExpression().accept(this);
        formatBuilder.append(";");
    }

    @Override
    public void visit(ArrayLength arrayLength) {
        formatBuilder.append("length(");
        arrayLength.getArray().accept(this);
        formatBuilder.append(")");
    }

    @Override
    public void visit(Join join) {
        formatBuilder.append("join(");
        join.getThreadId().accept(this);
        formatBuilder.append(")");
    }

    @Override
    public void visit(Synchronized synchronizedInstruction) {
        appendTabs();
        formatBuilder.append("synchronized (");
        synchronizedInstruction.getMutex().accept(this);
        formatBuilder.append(") {");

        currentTabs++;
        for (Statement statement: synchronizedInstruction.getCriticalSection()) {
            formatBuilder.append("\n");
            statement.accept(this);
        }
        currentTabs--;

        formatBuilder.append("\n");
        appendTabs();
        formatBuilder.append("}");
    }

    private void appendTabs() {
        for (int i = 0; i < currentTabs; i++) {
            formatBuilder.append("  ");
        }
    }

}