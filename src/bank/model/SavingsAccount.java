package bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Savings: must keep a minimum balance and earns monthly interest. */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final BigDecimal MINIMUM_BALANCE = money("500.00");
    public static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("0.05"); // 5%

    public SavingsAccount(String accountNumber, String holderName, BigDecimal openingDeposit) {
        super(accountNumber, holderName, openingDeposit);
    }

    @Override
    protected void validateOpeningDeposit(BigDecimal amount) {
        super.validateOpeningDeposit(amount);
        if (amount.compareTo(MINIMUM_BALANCE) < 0)
            throw new IllegalArgumentException("Savings account needs an opening deposit of at least "
                    + MINIMUM_BALANCE.toPlainString());
    }

    @Override
    protected BigDecimal getLowestAllowedBalance() {
        return MINIMUM_BALANCE;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    /** Adds one month of interest and returns the amount added. */
    public BigDecimal applyMonthlyInterest() {
        BigDecimal interest = getBalance()
                .multiply(ANNUAL_INTEREST_RATE)
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        if (interest.signum() > 0) {
            credit(interest, TransactionType.INTEREST, "Monthly interest");
        }
        return interest;
    }
}
