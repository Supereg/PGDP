public interface ProgramVisitor {

    void visit(Program program);


    void visit(Function function);


    void visit(Declaration declaration);


    void visit(Assignment assignment);

    void visit(Composite composite);

    void visit(IfThen ifThen);

    void visit(IfThenElse ifThenElse);

    void visit(While whileStatement);

    void visit(Return returnStatement);

    void visit(ExpressionStatement expressionStatement);


    void visit(Variable variable);

    void visit(Number number);

    void visit(Binary binary);

    void visit(Unary unary);

    void visit(Read read);

    void visit(Write write);

    void visit(Call call);


    void visit(True trueCondition);

    void visit(False falseCondition);

    void visit(BinaryCondition binaryCondition);

    void visit(Comparison comparison);

    void visit(UnaryCondition unaryCondition);

}