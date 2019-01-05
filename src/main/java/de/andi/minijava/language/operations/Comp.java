package de.andi.minijava.language.operations;

public enum Comp {

  Equals("==", true),
  NotEquals("!=", true),
  LessEqual("<=", true),
  Less("<", true),
  GreaterEqual(">=", false),
  Greater(">", false),
  ;

  private String symbol;
  private boolean rightToLeft;

  Comp(String symbol, boolean rightToLeft) {
    this.symbol = symbol;
    this.rightToLeft = rightToLeft;
  }

  public boolean isRightToLeft() {
    return rightToLeft;
  }

  @Override
  public String toString() {
    return symbol;
  }

}
