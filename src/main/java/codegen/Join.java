package codegen;

public class Join extends Expression {

  private Expression threadId;
  
  public Join(Expression threadId) {
    this.threadId = threadId;
  }

  public Expression getThreadId() {
    return threadId;
  }

  @Override
  public void accept(ProgramVisitor visitor) {
    visitor.visit(this);
  }

}
