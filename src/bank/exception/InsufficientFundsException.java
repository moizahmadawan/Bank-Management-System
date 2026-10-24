package bank.exception;

/** Thrown when a withdrawal or transfer would break the account's balance rules. */
public class InsufficientFundsException extends Exception {
    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(String message) {
        super(message);
    }
}
