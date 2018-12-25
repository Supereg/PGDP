package de.andi.minijava.language.operations;

public enum Bunop {

  Not("!"),
  ;

  private String symbol;

  Bunop(String symbol) {
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
