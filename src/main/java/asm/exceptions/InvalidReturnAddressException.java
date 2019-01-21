package asm.exceptions;

public class InvalidReturnAddressException extends InterpreterException {

  public InvalidReturnAddressException() {
    super("Invalid return address on stack; the stack has been destroyed by the program");
  }

}
