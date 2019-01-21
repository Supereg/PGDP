package codegen;

public enum Comp {

  Equals("==", false),
  NotEquals("!=", false),
  LessEqual("<=", true), // (a <= b) = !(a > b) = !(b < a)
  Less("<", false),
  GreaterEqual(">=", false), // (a >= b) = !(a < b)
  Greater(">", true), // (a > b) = (b < a)
  ;

  private String symbol;
  private boolean switchInput;

  Comp(String symbol, boolean switchInput) {
    this.symbol = symbol;
    this.switchInput = switchInput;
  }

  public boolean isSwitchInput() {
    return switchInput;
  }

  @Override
  public String toString() {
    return symbol;
  }

}
