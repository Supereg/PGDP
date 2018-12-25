package de.andi.minijava.language.operations;

public enum Comp {

  Equals("=="),
  NotEquals("!="),
  LessEqual("<="),
  Less("<"),
  GreaterEqual(">="),
  Greater(">"),
  ;

  private String symbol;

  Comp(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }

  @Override
  public String toString() {
    return symbol;
  }

}
