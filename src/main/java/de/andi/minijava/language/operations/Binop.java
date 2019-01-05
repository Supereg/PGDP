package de.andi.minijava.language.operations;

public enum Binop {

  Minus("-"),
  Plus("+"),
  MultiplicationOperator("*"),
  DivisionOperator("/"),
  Modulo("%"),
  ;

  private String symbol;

  Binop(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return symbol;
  }
}
