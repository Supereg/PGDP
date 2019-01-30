package asm;

public class Cmp extends Instruction {

    private CompareType operator;

    public Cmp(CompareType operator) {
        this.operator = operator;
    }

    public CompareType getOperator() {
        return operator;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}