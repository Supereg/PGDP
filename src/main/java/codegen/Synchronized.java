package codegen;

public class Synchronized extends Statement {

  private Expression mutex;
  private Statement[] criticalSection;

  public Synchronized(Expression mutex, Statement... criticalSection) {
    this.mutex = mutex;
    this.criticalSection = criticalSection;
  }

  public Expression getMutex() {
    return mutex;
  }

  public Statement[] getCriticalSection() {
    return criticalSection;
  }

  @Override
  public void accept(ProgramVisitor visitor) {
    visitor.visit(this);
  }

}
