# ATM Interface Project

## Overview
This project is a simple **ATM (Automated Teller Machine)** simulation written in Java. It demonstrates basic object‑oriented concepts such as classes, methods, and encapsulation. The goal is to give a student a hands‑on example of how a banking application might be structured.

## Project Structure
```
ATM.java          – Main application containing the ATM logic
README.md         – This documentation file
```

## How It Works
1. **Account** – Each user has an account with a balance. The balance is stored as a `double`.
2. **ATM** – The `ATM` class provides three simple operations:
   * `deposit(double amount)` – Adds money to the account.
   * `withdraw(double amount)` – Removes money if sufficient funds exist.
   * `checkBalance()` – Returns the current balance.
3. **Main** – The `main` method creates an `ATM` instance and demonstrates a few transactions.

### Example Code
```java
public class ATM {
    private double balance;

    public ATM(double initialBalance) {
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount);
        } else {
            System.out.println("Invalid deposit amount");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrew: " + amount);
        } else {
            System.out.println("Insufficient funds or invalid amount");
        }
    }

    public double checkBalance() {
        return balance;
    }

    public static void main(String[] args) {
        ATM myATM = new ATM(1000.0);
        myATM.deposit(200.0);
        myATM.withdraw(150.0);
        System.out.println("Current balance: " + myATM.checkBalance());
    }
}
```

### Running the Program
Compile and run the `ATM.java` file:
```
javac ATM.java
java ATM
```
You should see output similar to:
```
Deposited: 200.0
Withdrew: 150.0
Current balance: 1050.0
```

## What You Can Try
- Change the initial balance in the `main` method.
- Add more methods like `transfer` or `viewTransactionHistory`.
- Experiment with negative amounts to see how the program handles errors.

