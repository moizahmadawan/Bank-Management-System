package bank.service;

import bank.exception.AccountNotFoundException;
import bank.exception.InsufficientFundsException;
import bank.model.Account;
import bank.model.CurrentAccount;
import bank.model.SavingsAccount;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Business logic + saving/loading. The console UI only talks to this class. */
public class BankService {

    /** Everything that gets saved to disk. */
    private static class BankData implements Serializable {
        private static final long serialVersionUID = 1L;
        final Map<String, Account> accounts = new LinkedHashMap<>();
        int nextNumber = 1001;
    }

    private final Path file;
    private BankData data;

    public BankService(Path file) {
        this.file = file;
        this.data = load();
    }

    // ---------- accounts ----------

    public Account createAccount(boolean savings, String holder, BigDecimal opening) {
        String number = "PK" + data.nextNumber;
        Account account = savings
                ? new SavingsAccount(number, holder, opening)
                : new CurrentAccount(number, holder, opening);
        data.nextNumber++;
        data.accounts.put(number, account);
        save();
        return account;
    }

    public Account find(String accountNumber) throws AccountNotFoundException {
        Account a = data.accounts.get(accountNumber.trim().toUpperCase());
        if (a == null) throw new AccountNotFoundException(accountNumber.trim().toUpperCase());
        return a;
    }

    public List<Account> getAll() {
        return new ArrayList<>(data.accounts.values());
    }

    public void closeAccount(String accountNumber) throws AccountNotFoundException {
        Account a = find(accountNumber);
        if (a.getBalance().signum() != 0)
            throw new IllegalStateException("Balance must be exactly 0.00 to close an account (current: "
                    + a.getBalance().toPlainString() + ").");
        data.accounts.remove(a.getAccountNumber());
        save();
    }

    // ---------- money ----------

    public void deposit(String accountNumber, BigDecimal amount) throws AccountNotFoundException {
        find(accountNumber).deposit(amount);
        save();
    }

    public void withdraw(String accountNumber, BigDecimal amount)
            throws AccountNotFoundException, InsufficientFundsException {
        find(accountNumber).withdraw(amount);
        save();
    }

    public void transfer(String from, String to, BigDecimal amount)
            throws AccountNotFoundException, InsufficientFundsException {
        Account source = find(from);
        Account target = find(to);
        if (source == target)
            throw new IllegalArgumentException("Cannot transfer to the same account.");

        source.transferOut(amount, target.getAccountNumber());   // may throw, nothing changed yet
        target.transferIn(amount, source.getAccountNumber());
        save();
    }

    /** Applies one month of interest to every savings account. Returns total interest paid. */
    public BigDecimal applyInterestToAll() {
        BigDecimal total = BigDecimal.ZERO;
        for (Account a : data.accounts.values()) {
            if (a instanceof SavingsAccount s) {
                total = total.add(s.applyMonthlyInterest());
            }
        }
        save();
        return total;
    }

    // ---------- persistence ----------

    private BankData load() {
        if (!Files.exists(file)) return new BankData();
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            return (BankData) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.out.println("Warning: could not read saved data (" + e.getMessage() + "). Starting fresh.");
            return new BankData();
        }
    }

    private void save() {
        try {
            Path dir = file.toAbsolutePath().getParent();
            if (dir != null) Files.createDirectories(dir);
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
                out.writeObject(data);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save data: " + e.getMessage());
        }
    }
}
