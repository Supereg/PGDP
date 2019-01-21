package codegen.utils;

public class CGFunctionCall {

    private final String functionName;
    private final int instructionIndex;

    public CGFunctionCall(String functionName, int instructionIndex) {
        this.functionName = functionName;
        this.instructionIndex = instructionIndex;
    }

    public String getFunctionName() {
        return functionName;
    }

    public int getInstructionIndex() {
        return instructionIndex;
    }

    @Override
    public String toString() {
        return "CGFunctionCall{" +
                "functionName='" + functionName + '\'' +
                ", instructionIndex=" + instructionIndex +
                '}';
    }

}