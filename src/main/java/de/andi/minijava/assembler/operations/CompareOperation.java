package de.andi.minijava.assembler.operations;

public enum CompareOperation {

    EQUALS("="),
    LESS("<"),
    ;

    private String symbol;

    CompareOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return name();
    }

}