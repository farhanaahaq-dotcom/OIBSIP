import java.util.ArrayList;
import java.util.List;

public class Account {

    private final String accountId;
    private final String userId;
    private final String holderName;
    private final String pin;

    private double balance;

    private final ArrayList<Transaction> transactions;

    public Account(String accountId, String userId, String holderName, String pin, double openingBalance) {

        this.accountId = accountId;
        this.userId = userId;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = openingBalance;

        this.transactions = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUserId() {
        return userId;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {

        if (amount <= 0 || amount > balance) {
            return false;
        }

        balance -= amount;

        return true;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
}
