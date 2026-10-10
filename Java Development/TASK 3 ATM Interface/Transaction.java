import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final String details;
    private final LocalDateTime time;

    public Transaction(String type, double amount, double balanceAfter, String details) {

        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.details = details;

        this.time = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public String toString() {

        String stamp = time.format(DateTimeFormatter.ofPattern("dd MMM yyyy  hh:mm a"));

        return String.format("%-19s | %-10s | %10.2f | Balance: %10.2f | %s", stamp, type, amount, balanceAfter,
                details);
    }
}
