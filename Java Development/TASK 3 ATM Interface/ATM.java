import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class ATM extends JFrame {

    private final Bank bank;
    private Account currentAccount;

    private JLabel balanceLabel;
    private JPanel contentPanel;

    private final Border lineBorder = BorderFactory.createLineBorder(Color.GRAY, 1);

    private final Border fieldBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
            new EmptyBorder(6, 8, 6, 8));

    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            ATM atm = new ATM(new Bank());
            atm.setVisible(true);
        });
    }

    public ATM(Bank bank) {

        this.bank = bank;

        currency.setMaximumFractionDigits(2);
        currency.setMinimumFractionDigits(2);

        setTitle("HERO ATM");
        setSize(1000, 650);
        setMinimumSize(new Dimension(900, 580));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        showLogin();
    }

    private void showLogin() {

        getContentPane().removeAll();

        getContentPane().setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel();

        loginPanel.setPreferredSize(new Dimension(420, 430));

        loginPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(30, 40, 30, 40)));

        loginPanel.setLayout(new BorderLayout(0, 20));

        JLabel title = new JLabel("HERO ATM");

        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Secure Banking System");

        JPanel branding = new JPanel(new GridLayout(2, 1, 0, 5));
        branding.add(title);
        branding.add(subtitle);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel userLabel = new JLabel("USER ID");

        JTextField userField = new JTextField();

        setupField(userField);

        JLabel pinLabel = new JLabel("PIN");

        JPasswordField pinField = new JPasswordField();

        setupField(pinField);

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 0;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.insets = new Insets(0, 0, 6, 0);
        userLabel.setLabelFor(userField);
        pinLabel.setLabelFor(pinField);
        fieldConstraints.gridy = 0;
        fields.add(userLabel, fieldConstraints);
        fieldConstraints.gridy = 1;
        fields.add(userField, fieldConstraints);
        fieldConstraints.gridy = 2;
        fieldConstraints.insets = new Insets(12, 0, 6, 0);
        fields.add(pinLabel, fieldConstraints);
        fieldConstraints.gridy = 3;
        fieldConstraints.insets = new Insets(0, 0, 0, 0);
        fields.add(pinField, fieldConstraints);

        JButton loginButton = new JButton("LOGIN");

        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(loginButton);
        footer.add(Box.createVerticalStrut(12));

        JLabel demo = new JLabel("Demo: HERO01 / 1234");

        demo.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(demo);
        loginPanel.add(branding, BorderLayout.NORTH);
        loginPanel.add(fields, BorderLayout.CENTER);
        loginPanel.add(footer, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {

            String userId = userField.getText().trim();

            String pin = new String(pinField.getPassword()).trim();

            if (userId.isEmpty() || pin.isEmpty()) {

                showWarning("Missing Details", "Please enter User ID and PIN.");

                return;
            }

            Account account = bank.authenticate(userId, pin);

            if (account == null) {

                pinField.setText("");

                showError("Login Failed", "Invalid User ID or PIN.");

                pinField.requestFocus();

                return;
            }

            currentAccount = account;

            showDashboard();
        });

        getContentPane().add(loginPanel);

        refresh();
    }

    private void showDashboard() {

        getContentPane().removeAll();

        getContentPane().setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());

        header.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                new EmptyBorder(15, 20, 15, 20)));

        JPanel userInfo = new JPanel();

        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("HERO ATM");

        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel welcome = new JLabel("Welcome, " + currentAccount.getHolderName());

        userInfo.add(title);
        userInfo.add(welcome);

        balanceLabel = new JLabel("Balance: " + formatMoney(currentAccount.getBalance()));

        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        JButton logout = new JButton("LOG OUT");

        logout.addActionListener(e -> {

            int answer = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION);

            if (answer == JOptionPane.YES_OPTION) {

                currentAccount = null;

                showLogin();
            }
        });

        header.add(userInfo, BorderLayout.WEST);

        header.add(balanceLabel, BorderLayout.CENTER);

        header.add(logout, BorderLayout.EAST);

        JPanel menu = new JPanel(new GridLayout(5, 1, 8, 8));

        menu.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
                new EmptyBorder(20, 15, 20, 15)));

        menu.setPreferredSize(new Dimension(220, 0));

        JButton history = createMenuButton("Transaction History");

        JButton withdraw = createMenuButton("Withdraw");

        JButton deposit = createMenuButton("Deposit");

        JButton transfer = createMenuButton("Transfer");

        JButton quit = createMenuButton("Quit");

        history.addActionListener(e -> showHistory());

        withdraw.addActionListener(e -> showWithdraw());

        deposit.addActionListener(e -> showDeposit());

        transfer.addActionListener(e -> showTransfer());

        quit.addActionListener(e -> closeATM());

        menu.add(history);
        menu.add(withdraw);
        menu.add(deposit);
        menu.add(transfer);
        menu.add(quit);

        contentPanel = new JPanel(new BorderLayout());

        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        getContentPane().add(header, BorderLayout.NORTH);

        getContentPane().add(menu, BorderLayout.WEST);

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        showHome();

        refresh();
    }

    private void showHome() {

        contentPanel.removeAll();

        JPanel main = new JPanel();

        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("What would you like to do?");

        heading.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel description = new JLabel("Select a banking service from the menu.");

        main.add(heading);

        main.add(Box.createVerticalStrut(5));

        main.add(description);

        main.add(Box.createVerticalStrut(25));

        JPanel details = new JPanel(new GridLayout(1, 2, 15, 0));

        details.add(createInfoPanel("AVAILABLE BALANCE", formatMoney(currentAccount.getBalance())));

        details.add(createInfoPanel("ACCOUNT ID", currentAccount.getAccountId()));

        main.add(details);

        main.add(Box.createVerticalStrut(20));

        JPanel session = new JPanel(new BorderLayout());

        session.setBorder(BorderFactory.createCompoundBorder(lineBorder, new EmptyBorder(18, 20, 18, 20)));

        session.add(new JLabel("Secure session is active. " + "Choose a service to continue."), BorderLayout.CENTER);

        main.add(session);

        contentPanel.add(main, BorderLayout.NORTH);

        refresh();
    }

    private void showWithdraw() {

        showAmountForm("WITHDRAW CASH", "Enter the amount to withdraw.", "WITHDRAW", amount -> {

            if (amount <= 0) {

                showWarning("Invalid Amount", "Amount must be greater than ₹0.");

                return;
            }

            if (amount > currentAccount.getBalance()) {

                showError("Insufficient Funds", "Your current balance is " + formatMoney(currentAccount.getBalance()));

                return;
            }

            currentAccount.withdraw(amount);

            currentAccount.addTransaction(
                    new Transaction("WITHDRAW", amount, currentAccount.getBalance(), "Cash withdrawal"));

            updateBalance();

            showSuccess("Withdrawal Successful",
                    "Please collect your cash.\n\n" + "Remaining Balance: " + formatMoney(currentAccount.getBalance()));
        });
    }

    private void showDeposit() {

        showAmountForm("DEPOSIT MONEY", "Enter the amount to deposit.", "DEPOSIT", amount -> {

            if (amount <= 0) {

                showWarning("Invalid Amount", "Amount must be greater than ₹0.");

                return;
            }

            currentAccount.deposit(amount);

            currentAccount
                    .addTransaction(new Transaction("DEPOSIT", amount, currentAccount.getBalance(), "Cash deposit"));

            updateBalance();

            showSuccess("Deposit Successful", formatMoney(amount) + " deposited successfully.\n\n" + "New Balance: "
                    + formatMoney(currentAccount.getBalance()));
        });
    }

    private void showAmountForm(String title, String description, String buttonText, AmountAction action) {

        contentPanel.removeAll();

        JPanel form = createFormPanel(title, description);

        JTextField amountField = new JTextField();

        setupField(amountField);

        addFormLabel(form, "AMOUNT");

        form.add(Box.createVerticalStrut(6));

        form.add(amountField);

        form.add(Box.createVerticalStrut(20));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton actionButton = new JButton(buttonText);

        JButton cancel = new JButton("CANCEL");

        actionButton.addActionListener(e -> {

            Double amount = readAmount(amountField.getText());

            if (amount == null) {

                showWarning("Invalid Amount", "Please enter a valid number.");

                amountField.requestFocus();

                return;
            }

            action.run(amount);
        });

        cancel.addActionListener(e -> showHome());

        buttons.add(actionButton);
        buttons.add(cancel);

        form.add(buttons);

        contentPanel.add(form, BorderLayout.NORTH);

        refresh();

        amountField.requestFocus();
    }

    private void showTransfer() {

        contentPanel.removeAll();

        JPanel form = createFormPanel("TRANSFER MONEY", "Transfer money to another HERO ATM account.");

        JTextField accountField = new JTextField();

        JTextField amountField = new JTextField();

        setupField(accountField);
        setupField(amountField);

        addFormLabel(form, "RECIPIENT ACCOUNT ID");

        form.add(Box.createVerticalStrut(6));

        form.add(accountField);

        form.add(Box.createVerticalStrut(15));

        addFormLabel(form, "TRANSFER AMOUNT");

        form.add(Box.createVerticalStrut(6));

        form.add(amountField);

        form.add(Box.createVerticalStrut(20));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton transfer = new JButton("CONFIRM TRANSFER");

        JButton cancel = new JButton("CANCEL");

        transfer.addActionListener(e -> {

            String accountId = accountField.getText().trim();

            Double amount = readAmount(amountField.getText());

            if (accountId.isEmpty()) {

                showWarning("Account Required", "Enter the recipient account ID.");

                accountField.requestFocus();

                return;
            }

            if (amount == null || amount <= 0) {

                showWarning("Invalid Amount", "Enter a valid transfer amount.");

                amountField.requestFocus();

                return;
            }

            Account receiver = bank.findByAccountId(accountId);

            if (receiver == null) {

                showError("Account Not Found", "No account exists with ID: " + accountId);

                return;
            }

            if (receiver == currentAccount) {

                showWarning("Invalid Transfer", "You cannot transfer money to your own account.");

                return;
            }

            if (amount > currentAccount.getBalance()) {

                showError("Insufficient Funds", "Your balance is only " + formatMoney(currentAccount.getBalance()));

                return;
            }

            boolean success = bank.transfer(currentAccount, receiver, amount);

            if (!success) {

                showError("Transfer Failed", "The transfer could not be completed.");

                return;
            }

            currentAccount.addTransaction(
                    new Transaction("TRANSFER", amount, currentAccount.getBalance(), "To " + receiver.getAccountId()));

            receiver.addTransaction(new Transaction("RECEIVED", amount, receiver.getBalance(),
                    "From " + currentAccount.getAccountId()));

            updateBalance();

            showSuccess("Transfer Successful", formatMoney(amount) + " transferred successfully.\n\n" + "Recipient: "
                    + receiver.getAccountId() + "\nRemaining Balance: " + formatMoney(currentAccount.getBalance()));
        });

        cancel.addActionListener(e -> showHome());

        buttons.add(transfer);
        buttons.add(cancel);

        form.add(buttons);

        contentPanel.add(form, BorderLayout.NORTH);

        refresh();

        accountField.requestFocus();
    }

    private void showHistory() {

        contentPanel.removeAll();

        JPanel main = new JPanel(new BorderLayout(0, 15));

        JLabel title = new JLabel("TRANSACTION HISTORY");

        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        main.add(title, BorderLayout.NORTH);

        List<Transaction> transactions = currentAccount.getTransactions();

        if (transactions.isEmpty()) {

            JPanel empty = new JPanel(new GridBagLayout());

            empty.setBorder(lineBorder);

            empty.add(new JLabel("No transactions available."));

            main.add(empty, BorderLayout.CENTER);

        } else {

            String[] columns = { "DATE / TIME", "TYPE", "AMOUNT", "BALANCE", "DETAILS" };

            Object[][] data = new Object[transactions.size()][5];

            for (int i = 0; i < transactions.size(); i++) {

                Transaction t = transactions.get(i);

                data[i][0] = t.getTime().format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a"));

                data[i][1] = t.getType();

                data[i][2] = formatMoney(t.getAmount());

                data[i][3] = formatMoney(t.getBalanceAfter());

                data[i][4] = t.getDetails();
            }

            JTable table = new JTable(data, columns);

            table.setRowHeight(32);

            table.setBorder(lineBorder);

            table.getTableHeader().setReorderingAllowed(false);

            JScrollPane scroll = new JScrollPane(table);

            scroll.setBorder(lineBorder);

            main.add(scroll, BorderLayout.CENTER);
        }

        contentPanel.add(main, BorderLayout.CENTER);

        refresh();
    }

    private JPanel createFormPanel(String title, String description) {

        JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createCompoundBorder(lineBorder, new EmptyBorder(25, 30, 25, 30)));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel(title);

        heading.setFont(new Font("SansSerif", Font.BOLD, 24));

        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);

        panel.add(Box.createVerticalStrut(5));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(descriptionLabel);

        panel.add(Box.createVerticalStrut(25));

        return panel;
    }

    private void addFormLabel(JPanel form, String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(label);
    }

    private JPanel createInfoPanel(String title, String value) {

        JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createCompoundBorder(lineBorder, new EmptyBorder(20, 20, 20, 20)));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel valueLabel = new JLabel(value);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(8));

        panel.add(valueLabel);

        return panel;
    }

    private JButton createMenuButton(String text) {

        JButton button = new JButton(text);

        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.setBorder(BorderFactory.createCompoundBorder(lineBorder, new EmptyBorder(10, 12, 10, 12)));

        return button;
    }

    private void setupField(JTextField field) {

        field.setFont(new Font("SansSerif", Font.PLAIN, 14));

        field.setBorder(fieldBorder);

        field.setPreferredSize(new Dimension(280, 40));
        field.setMinimumSize(new Dimension(120, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private Double readAmount(String text) {

        try {

            if (text == null || text.trim().isEmpty()) {

                return null;
            }

            double amount = Double.parseDouble(text.trim());
            return Double.isFinite(amount) ? amount : null;

        } catch (NumberFormatException e) {

            return null;
        }
    }

    private String formatMoney(double amount) {

        return currency.format(amount);
    }

    private void updateBalance() {

        balanceLabel.setText("Balance: " + formatMoney(currentAccount.getBalance()));
    }

    private void showWarning(String title, String message) {

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String title, String message) {

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String title, String message) {

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);

        showHome();
    }

    private void closeATM() {

        int answer = JOptionPane.showConfirmDialog(this, "Are you sure you want to close HERO ATM?", "Close ATM",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {

            JOptionPane.showMessageDialog(this, "Thank you for using HERO ATM.\nHave a nice day!", "Session Ended",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
        }
    }

    private void refresh() {

        getContentPane().revalidate();
        getContentPane().repaint();
    }

    @FunctionalInterface
    private interface AmountAction {

        void run(double amount);
    }
}
