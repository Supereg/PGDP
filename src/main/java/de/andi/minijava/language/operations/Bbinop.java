package de.andi.minijava.language.operations;

public enum Bbinop {

  And("&&"),
  Or("||"),
  ;

  private String symbol;

  Bbinop(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return symbol;
  }

}
