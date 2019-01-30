package asm;

public enum CompareType {

    EQ("EQUALS", "="),
    LT("LESS", "<"),
    ;

    private String name;
    private String symbol;

    CompareType(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return name;
    }

}