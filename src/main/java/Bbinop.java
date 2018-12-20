public enum Bbinop {

  And("&&"),
  Or("||"),
  ;

  private String symbol;

  Bbinop(String symbol) {
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
