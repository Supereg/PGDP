package asm.exceptions;

public class InvalidMethodAddressException extends InterpreterException {

  public InvalidMethodAddressException() {
    super("Invalid method address");
  }

}
