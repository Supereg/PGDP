package codegen;

public class SwitchCase {

    private final Number number;
    private Statement caseStatement;

    public SwitchCase(Number number, Statement caseStatement) {
        this.number = number;
        this.caseStatement = caseStatement;
    }

    public Number getNumber() {
        return number;
    }

    public Statement getCaseStatement() {
        return caseStatement;
    }

}