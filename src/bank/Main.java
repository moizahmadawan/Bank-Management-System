package bank;

import bank.exception.AccountNotFoundException;
import bank.exception.InsufficientFundsException;
import bank.model.Account;
import bank.model.Transaction;
import bank.service.BankService;
import bank.util.ConsoleInput;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

public class Main {
    private static final BankService bank = new BankService(Path.of("data", "bank.dat"));
    private static final ConsoleInput in = new ConsoleInput();

    public static void main(String[] args) {
        try {
            run();
        } catch (NoSuchElementException e) {
            // input ended - exit quietly
        }
    }

    private static void run() {
        while (true) {
            showMenu();
            int choice = in.readInt("Choice: ", 0, 9);
            System.out.println();
            if (choice == 0) {
                System.out.println("Thank you for banking with us. Goodbye!");
                return;
            }
            try {
                switch (choice) {
                    case 1 -> createAccount();
                    case 2 -> deposit();
                    case 3 -> withdraw();
                    case 4 -> transfer();
                    case 5 -> showDetails();
                    case 6 -> showStatement();
                    case 7 -> listAccounts();
                    case 8 -> applyInterest();
                    case 9 -> closeAccount();
                }
            } catch (AccountNotFoundException | InsufficientFundsException
                     | IllegalArgumentException | IllegalStateException e) {
                System.out.println("  ERROR: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n========== BANK MANAGEMENT SYSTEM ==========");
        System.out.println(" 1. Create account");
        System.out.println(" 2. Deposit");
        System.out.println(" 3. Withdraw");
        System.out.println(" 4. Transfer money");
        System.out.println(" 5. Account details");
        System.out.println(" 6. Mini statement (last 10)");
        System.out.println(" 7. List all accounts");
        System.out.println(" 8. Apply monthly interest (savings)");
        System.out.println(" 9. Close account");
        System.out.println(" 0. Exit");
    }

    private static void createAccount() {
        int type = in.readInt("Type (1 = Savings, 2 = Current): ", 1, 2);
        String name = in.readLine("Holder name: ");
        BigDecimal opening = in.readAmount("Opening deposit: ");
        Account a = bank.createAccount(type == 1, name, opening);
        System.out.println("  Account created. Account number: " + a.getAccountNumber());
    }

    private static void deposit() throws AccountNotFoundException {
        String no = in.readLine("Account number: ");
        bank.find(no);   // fail early if the account does not exist
        bank.deposit(no, in.readAmount("Amount: "));
        System.out.println("  Deposit successful. New balance: " + bank.find(no).getBalance().toPlainString());
    }

    private static void withdraw() throws AccountNotFoundException, InsufficientFundsException {
        String no = in.readLine("Account number: ");
        bank.find(no);
        bank.withdraw(no, in.readAmount("Amount: "));
        System.out.println("  Withdrawal successful. New balance: " + bank.find(no).getBalance().toPlainString());
    }

    private static void transfer() throws AccountNotFoundException, InsufficientFundsException {
        String from = in.readLine("From account: ");
        bank.find(from);
        String to = in.readLine("To account: ");
        bank.find(to);
        bank.transfer(from, to, in.readAmount("Amount: "));
        System.out.println("  Transfer successful. Your balance: " + bank.find(from).getBalance().toPlainString());
    }

    private static void showDetails() throws AccountNotFoundException {
        Account a = bank.find(in.readLine("Account number: "));
        System.out.println("  Account number : " + a.getAccountNumber());
        System.out.println("  Holder         : " + a.getHolderName());
        System.out.println("  Type           : " + a.getAccountType());
        System.out.println("  Balance        : " + a.getBalance().toPlainString());
        System.out.println("  Transactions   : " + a.getTransactions().size());
    }

    private static void showStatement() throws AccountNotFoundException {
        Account a = bank.find(in.readLine("Account number: "));
        List<Transaction> all = a.getTransactions();
        int from = Math.max(0, all.size() - 10);
        System.out.println("  Statement for " + a.getAccountNumber() + " (" + a.getHolderName() + ")");
        System.out.printf("  %-17s %-13s %12s %12s  %s%n", "Date", "Type", "Amount", "Balance", "Note");
        for (Transaction t : all.subList(from, all.size())) {
            System.out.println("  " + t);
        }
    }

    private static void listAccounts() {
        List<Account> all = bank.getAll();
        if (all.isEmpty()) { System.out.println("  No accounts yet."); return; }
        System.out.printf("  %-8s %-22s %-8s %14s%n", "Number", "Holder", "Type", "Balance");
        all.forEach(a -> System.out.println("  " + a));
    }

    private static void applyInterest() {
        BigDecimal total = bank.applyInterestToAll();
        System.out.println("  Interest applied to savings accounts. Total paid: " + total.toPlainString());
    }

    private static void closeAccount() throws AccountNotFoundException {
        String no = in.readLine("Account number: ");
        Account a = bank.find(no);
        String confirm = in.readLine("  Close account of " + a.getHolderName() + "? (y/n): ");
        if (confirm.equalsIgnoreCase("y")) {
            bank.closeAccount(no);
            System.out.println("  Account closed.");
        } else {
            System.out.println("  Cancelled.");
        }
    }
}
