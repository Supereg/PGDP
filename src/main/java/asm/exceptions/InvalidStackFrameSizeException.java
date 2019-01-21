package asm.exceptions;

public class InvalidStackFrameSizeException extends InterpreterException {

  public InvalidStackFrameSizeException() {
    super("Invalid stack frame size");
  }

}
