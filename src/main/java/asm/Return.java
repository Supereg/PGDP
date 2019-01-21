package asm;

public class Return extends Instruction { // RETURN

    private int variableAndArgumentCount;

    public Return(int variableAndArgumentCount) {
        this.variableAndArgumentCount = variableAndArgumentCount;
    }

    public int getVariableAndArgumentCount() {
        return variableAndArgumentCount;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }

}