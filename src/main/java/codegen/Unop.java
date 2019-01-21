package codegen;

public enum Unop {

  Minus("-");

  private String symbol;

  Unop(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return symbol;
  }

}
