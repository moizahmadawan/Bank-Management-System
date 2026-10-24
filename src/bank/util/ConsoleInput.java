package bank.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

/** Safe console input: asks again on invalid input. */
public class ConsoleInput {
    private final Scanner scanner = new Scanner(System.in);

    /** Throws NoSuchElementException when input ends (Ctrl+Z / piped input finished). */
    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) { }
            System.out.println("  Please enter a number between " + min + " and " + max + ".");
        }
    }

    public BigDecimal readAmount(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                BigDecimal v = new BigDecimal(s);
                if (v.signum() > 0) return v.setScale(2, RoundingMode.UNNECESSARY);
                System.out.println("  Amount must be greater than zero.");
            } catch (NumberFormatException | ArithmeticException e) {
                System.out.println("  Enter a valid amount (max 2 decimal places), e.g. 1500 or 250.75");
            }
        }
    }
}
