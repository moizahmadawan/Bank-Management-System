package bank.model;

import java.math.BigDecimal;

/** Current account: no minimum balance, but allows an overdraft up to a limit. */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;

    public static final BigDecimal OVERDRAFT_LIMIT = money("10000.00");

    public CurrentAccount(String accountNumber, String holderName, BigDecimal openingDeposit) {
        super(accountNumber, holderName, openingDeposit);
    }

    @Override
    protected BigDecimal getLowestAllowedBalance() {
        return OVERDRAFT_LIMIT.negate();
    }

    @Override
    public String getAccountType() {
        return "CURRENT";
    }
}
