# Bank Management System (Java)

A console-based banking application written in **pure Java** (no external libraries). It supports two account types with different rules, money transfers, interest, transaction history, and saves everything to disk automatically.

## Features

- Create **Savings** and **Current** accounts (auto-generated account numbers like `PK1001`)
- Deposit, withdraw and **transfer** money between accounts
- **Mini statement** (last 10 transactions with date, type, amount and balance)
- Apply **monthly interest** to all savings accounts
- Close an account (only when the balance is exactly zero)
- Strong input validation and clear error messages
- Data is saved automatically to `data/bank.dat` and loaded on the next start

## Account Rules

| | Savings | Current |
|---|---|---|
| Minimum opening deposit | 500.00 | any amount > 0 |
| Lowest allowed balance | 500.00 | -10,000.00 (overdraft) |
| Interest | 5% per year, paid monthly | none |

## OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| Abstraction (abstract class) | `Account` defines the rules, subclasses fill them in |
| Inheritance | `SavingsAccount` and `CurrentAccount` extend `Account` |
| Polymorphism | `getLowestAllowedBalance()` and `getAccountType()` behave differently per account type |
| Encapsulation | Private fields, read-only transaction list (`Collections.unmodifiableList`) |
| Custom checked exceptions | `InsufficientFundsException`, `AccountNotFoundException` |
| Enums | `TransactionType` |
| Immutable class | `Transaction` |
| Collections | `LinkedHashMap`, `ArrayList` |
| `BigDecimal` for money | Avoids floating-point rounding errors |
| Serialization | `BankService` saves and loads all accounts |
| Modern Java (17) | Switch expressions, pattern matching for `instanceof` |
| Layered design | `model` / `service` / `util` / UI (`Main`) |

## Project Structure

```
BankManagementSystem/
├── src/bank/
│   ├── Main.java                         # Console menu
│   ├── model/
│   │   ├── Account.java                  # Abstract base class
│   │   ├── SavingsAccount.java
│   │   ├── CurrentAccount.java
│   │   ├── Transaction.java
│   │   └── TransactionType.java
│   ├── service/
│   │   └── BankService.java              # Business logic + save/load
│   ├── exception/
│   │   ├── AccountNotFoundException.java
│   │   └── InsufficientFundsException.java
│   └── util/
│       └── ConsoleInput.java             # Safe input helpers
├── run.bat                               # Windows
├── run.sh                                # Linux / macOS
├── .gitignore
├── LICENSE
└── README.md
```

## Requirements

- **JDK 17 or newer** ([download](https://adoptium.net/))

Check your version:

```bash
java -version
javac -version
```

## Run

**Windows:** double-click `run.bat`, or in a terminal:

```bat
run.bat
```

**Linux / macOS:**

```bash
./run.sh
```

**Manually:**

```bash
mkdir out
javac -d out $(find src -name "*.java")
java -cp out bank.Main
```

## Sample Session

```
Choice: 1
Type (1 = Savings, 2 = Current): 1
Holder name: Ali Raza
Opening deposit: 5000
  Account created. Account number: PK1001

Choice: 3
Account number: PK1001
Amount: 4600
  ERROR: Insufficient funds. Balance: 5000.00, lowest allowed balance: 500.00
```

## Possible Improvements

- PIN / login for account holders
- Replace file serialization with a database (JDBC + MySQL or SQLite)
- JUnit tests
- JavaFX or Swing GUI
- Convert to a Spring Boot REST API

## Author

**Malik Moiz Ahmad Awan**
BS Computer Science, University of Gujrat

Licensed under the MIT License.
