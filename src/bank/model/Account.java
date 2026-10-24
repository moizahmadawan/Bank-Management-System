package bank.model;

import bank.exception.InsufficientFundsException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for every account type (abstraction).
 * Subclasses decide the rules: how low the balance may go and what the type is called.
 */
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final String holderName;
    private BigDecimal balance = BigDecimal.ZERO.setScale(2);
    private final List<Transaction> transactions = new ArrayList<>();

    protected Account(String accountNumber, String holderName, BigDecimal openingDeposit) {
        if (holderName == null || holderName.isBlank())
            throw new IllegalArgumentException("Holder name cannot be empty.");
        this.accountNumber = accountNumber;
        this.holderName = holderName.trim();
        validateOpeningDeposit(openingDeposit);
        credit(openingDeposit, TransactionType.DEPOSIT, "Opening deposit");
    }

    // ---------- rules provided by subclasses (polymorphism) ----------

    /** The lowest balance this account is allowed to reach after a debit. */
    protected abstract BigDecimal getLowestAllowedBalance();

    public abstract String getAccountType();

    /** Subclasses may add their own rule for the first deposit. */
    protected void validateOpeningDeposit(BigDecimal amount) {
        validateAmount(amount);
    }

    // ---------- public operations ----------

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        credit(amount, TransactionType.DEPOSIT, "Cash deposit");
    }

    public void withdraw(BigDecimal amount) throws InsufficientFundsException {
        validateAmount(amount);
        debit(amount, TransactionType.WITHDRAWAL, "Cash withdrawal");
    }

    public void transferOut(BigDecimal amount, String toAccount) throws InsufficientFundsException {
        validateAmount(amount);
        debit(amount, TransactionType.TRANSFER_OUT, "Transfer to " + toAccount);
    }

    public void transferIn(BigDecimal amount, String fromAccount) {
        validateAmount(amount);
        credit(amount, TransactionType.TRANSFER_IN, "Transfer from " + fromAccount);
    }

    // ---------- internals ----------

    protected void credit(BigDecimal amount, TransactionType type, String note) {
        balance = balance.add(amount);
        transactions.add(new Transaction(type, amount, balance, note));
    }

    private void debit(BigDecimal amount, TransactionType type, String note) throws InsufficientFundsException {
        BigDecimal after = balance.subtract(amount);
        if (after.compareTo(getLowestAllowedBalance()) < 0) {
            throw new InsufficientFundsException(String.format(
                    "Insufficient funds. Balance: %s, lowest allowed balance: %s",
                    balance.toPlainString(), getLowestAllowedBalance().toPlainString()));
        }
        balance = after;
        transactions.add(new Transaction(type, amount, balance, note));
    }

    protected static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero.");
        if (amount.scale() > 2)
            throw new IllegalArgumentException("Amount can have at most 2 decimal places.");
    }

    protected static BigDecimal money(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    // ---------- getters ----------

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public BigDecimal getBalance() { return balance; }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    @Override
    public String toString() {
        return String.format("%-8s %-22s %-8s %14s",
                accountNumber, holderName, getAccountType(), balance.toPlainString());
    }
}
