package bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One entry in an account's history. Immutable. */
public final class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final LocalDateTime time;
    private final String note;

    public Transaction(TransactionType type, BigDecimal amount, BigDecimal balanceAfter, String note) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.note = note;
        this.time = LocalDateTime.now();
    }

    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getTime() { return time; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        return String.format("%-17s %-13s %12s %12s  %s",
                time.format(FORMAT), type, amount.toPlainString(), balanceAfter.toPlainString(), note);
    }
}
