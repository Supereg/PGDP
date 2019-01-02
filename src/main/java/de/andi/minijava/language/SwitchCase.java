package de.andi.minijava.language;

public class SwitchCase {

    private final int number;
    private Statement caseStatement;

    public SwitchCase(int number, Statement caseStatement) {
        this.number = number;
        this.caseStatement = caseStatement;
    }

    public int getNumber() {
        return number;
    }

    public Statement getCaseStatement() {
        return caseStatement;
    }

}