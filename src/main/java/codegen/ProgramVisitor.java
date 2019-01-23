package codegen;

public interface ProgramVisitor {

    void visit(Program program);


    void visit(Function function);


    void visit(Declaration declaration);


    void visit(Assignment assignment);

    void visit(Composite composite);

    void visit(IfThen ifThen);

    void visit(IfThenElse ifThenElse);

    void visit(While whileStatement);

    void visit(Switch switchStatement);

    void visit(Return returnStatement);

    void visit(Break breakStatement);

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


    void visit(ArrayAllocator arrayAllocator);

    void visit(ArrayAccess arrayAccess);

    void visit(ArrayIndexAssignment arrayIndexAssignment);

    void visit(ArrayLength arrayLength);

}