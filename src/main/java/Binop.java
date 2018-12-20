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

  public String getSymbol() {
    return symbol;
  }

  @Override
  public String toString() {
    return symbol;
  }
}
