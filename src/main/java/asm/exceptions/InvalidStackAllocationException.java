package asm.exceptions;

public class InvalidStackAllocationException extends InterpreterException {

  public InvalidStackAllocationException() {
    super("Invalid stack allocation");
  }

}
