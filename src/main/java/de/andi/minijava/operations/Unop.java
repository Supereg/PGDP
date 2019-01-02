package de.andi.minijava.language.operations;

public enum Unop {

  Minus("-");

  private String symbol;

  Unop(String symbol) {
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
