package asm.exceptions;

// best thing I ever created
public class CannotUnlockUnlockedLockException extends InterpreterException {

    public CannotUnlockUnlockedLockException() {
        super("Tried unlocking a lock that wasn't locked!");
    }

}