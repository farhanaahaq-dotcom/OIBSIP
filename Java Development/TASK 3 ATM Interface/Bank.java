import java.util.HashMap;
import java.util.Map;

public class Bank {

    private final Map<String, Account> accounts = new HashMap<>();

    public Bank() {

        accounts.put("HERO1001", new Account("HERO1001", "HERO01", "Arun Kumar", "1234", 25000.00));

        accounts.put("HERO1002", new Account("HERO1002", "HERO02", "Priya S", "4321", 18000.00));

        accounts.put("HERO1003", new Account("HERO1003", "HERO03", "Karthik R", "2468", 12000.00));
    }

    public Account authenticate(String userId, String pin) {

        for (Account account : accounts.values()) {

            if (account.getUserId().equalsIgnoreCase(userId) && account.getPin().equals(pin)) {

                return account;
            }
        }

        return null;
    }

    public Account findByAccountId(String accountId) {

        if (accountId == null) {
            return null;
        }

        return accounts.get(accountId.trim().toUpperCase());
    }

    public boolean transfer(Account sender, Account receiver, double amount) {

        if (sender == null || receiver == null || sender == receiver || amount <= 0) {

            return false;
        }

        if (!sender.withdraw(amount)) {
            return false;
        }

        receiver.deposit(amount);

        return true;
    }
}
