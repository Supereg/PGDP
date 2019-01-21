package asm.exceptions;

public class InvalidJumpTargetException extends InterpreterException {

  public InvalidJumpTargetException() {
    super("Invalid jump target");
  }

}
