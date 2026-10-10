import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class NGM extends JFrame {

    private enum Difficulty {
        EASY("Easy", 1, 50),
        MEDIUM("Medium", 1, 100),
        HARD("Hard", 1, 200);

        private final String name;
        private final int minimum;
        private final int maximum;

        Difficulty(String name, int minimum, int maximum) {
            this.name = name;
            this.minimum = minimum;
            this.maximum = maximum;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final Random random = new Random();

    private Difficulty currentDifficulty = Difficulty.MEDIUM;

    private int secretNumber;
    private int attempts;
    private int roundNumber = 1;
    private int wins;
    private int losses;
    private int currentScore;
    private int bestScore;

    private JComboBox<Difficulty> difficultyBox;
    private JLabel rangeLabel;
    private JLabel attemptLabel;
    private JLabel roundLabel;
    private JLabel scoreLabel;
    private JLabel bestScoreLabel;
    private JLabel accuracyLabel;
    private JLabel resultLabel;
    private JLabel hintLabel;

    private JTextField guessField;

    private JButton guessButton;
    private JButton playAgainButton;
    private JButton resetButton;

    private JTextArea historyArea;

    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;
    private final Color PRIMARY = new Color(55, 89, 182);
    private final Color TEXT = new Color(35, 39, 47);
    private final Color MUTED = new Color(105, 112, 125);
    private final Color BORDER = new Color(220, 224, 231);
    private final Color SUCCESS = new Color(42, 145, 82);
    private final Color ERROR = new Color(190, 70, 70);

    public NGM() {
        setTitle("Number Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 680);
        setLocationRelativeTo(null);
        setResizable(false);

        buildInterface();
        startNewRound();
    }

    private int generateNumber() {
        return random.nextInt(
                currentDifficulty.maximum - currentDifficulty.minimum + 1
        ) + currentDifficulty.minimum;
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(25, 25, 25, 25));

        root.add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.setOpaque(false);

        center.add(createGameCard());
        center.add(createHistoryCard());

        root.add(center, BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Number Guessing Game");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Find the hidden number before your 10 attempts run out."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitle);

        panel.add(text, BorderLayout.WEST);

        return panel;
    }

    private JPanel createGameCard() {
        JPanel card = createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel difficultyTitle = new JLabel("Difficulty");
        difficultyTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        difficultyTitle.setForeground(TEXT);

        difficultyBox = new JComboBox<>(Difficulty.values());
        difficultyBox.setSelectedItem(Difficulty.MEDIUM);
        difficultyBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        difficultyBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        difficultyBox.addActionListener(e -> changeDifficulty());

        card.add(difficultyTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(difficultyBox);

        card.add(Box.createVerticalStrut(18));

        rangeLabel = new JLabel();
        styleInfoLabel(rangeLabel);
        card.add(rangeLabel);

        card.add(Box.createVerticalStrut(20));

        JLabel inputTitle = new JLabel("Enter your guess");
        inputTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputTitle.setForeground(TEXT);
        card.add(inputTitle);

        card.add(Box.createVerticalStrut(8));

        guessField = new JTextField();
        guessField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        guessField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        guessField.addActionListener(this::handleGuess);

        card.add(guessField);

        card.add(Box.createVerticalStrut(12));

        guessButton = createButton("Guess", PRIMARY);
        guessButton.addActionListener(this::handleGuess);

        card.add(guessButton);

        card.add(Box.createVerticalStrut(18));

        resultLabel = new JLabel("Make your first guess!");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultLabel.setForeground(PRIMARY);
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(resultLabel);

        card.add(Box.createVerticalStrut(8));

        hintLabel = new JLabel("");
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hintLabel.setForeground(MUTED);
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(hintLabel);

        card.add(Box.createVerticalStrut(18));

        attemptLabel = new JLabel();
        roundLabel = new JLabel();
        scoreLabel = new JLabel();
        bestScoreLabel = new JLabel();
        accuracyLabel = new JLabel();

        styleInfoLabel(attemptLabel);
        styleInfoLabel(roundLabel);
        styleInfoLabel(scoreLabel);
        styleInfoLabel(bestScoreLabel);
        styleInfoLabel(accuracyLabel);

        card.add(attemptLabel);
        card.add(Box.createVerticalStrut(5));

        card.add(roundLabel);
        card.add(Box.createVerticalStrut(5));

        card.add(scoreLabel);
        card.add(Box.createVerticalStrut(5));

        card.add(bestScoreLabel);
        card.add(Box.createVerticalStrut(5));

        card.add(accuracyLabel);

        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createHistoryCard() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Game History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        historyArea.setForeground(TEXT);
        historyArea.setBackground(new Color(250, 251, 253));
        historyArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFooter() {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        panel.setOpaque(false);

        playAgainButton = createButton("Play Again", PRIMARY);
        playAgainButton.addActionListener(e -> playAgain());

        resetButton = createButton(
                "Reset Game",
                new Color(100, 108, 120)
        );
        resetButton.addActionListener(e -> resetGame());

        panel.add(playAgainButton);
        panel.add(resetButton);

        return panel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(20, 20, 20, 20)
                )
        );

        return panel;
    }

    private JButton createButton(String text, Color background) {
        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(125, 40));

        return button;
    }

    private void styleInfoLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void startNewRound() {
        attempts = 0;
        currentScore = 0;
        secretNumber = generateNumber();

        guessField.setText("");
        guessField.setEnabled(true);
        guessButton.setEnabled(true);

        resultLabel.setText("Make your first guess!");
        resultLabel.setForeground(PRIMARY);

        hintLabel.setText("");

        updateInformation();

        SwingUtilities.invokeLater(
                () -> guessField.requestFocusInWindow()
        );
    }

    private void handleGuess(ActionEvent event) {
        if (!guessButton.isEnabled()) {
            return;
        }

        String input = guessField.getText().trim();

        if (input.isEmpty()) {
            showMessage(
                    "Please enter a number.",
                    ERROR
            );
            return;
        }

        int guess;

        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            showMessage(
                    "Please enter a valid whole number.",
                    ERROR
            );
            return;
        }

        if (guess < currentDifficulty.minimum
                || guess > currentDifficulty.maximum) {

            showMessage(
                    "Enter a number between "
                            + currentDifficulty.minimum
                            + " and "
                            + currentDifficulty.maximum
                            + ".",
                    ERROR
            );

            return;
        }

        attempts++;

        if (guess == secretNumber) {
            handleWin();
            return;
        }

        if (guess > secretNumber) {
            resultLabel.setText("Too High!");
            resultLabel.setForeground(ERROR);
        } else {
            resultLabel.setText("Too Low!");
            resultLabel.setForeground(PRIMARY);
        }

        hintLabel.setText("");

        updateInformation();

        if (attempts >= 10) {
            handleLoss();
            return;
        }

        guessField.setText("");
        guessField.requestFocusInWindow();
    }

    private void handleWin() {
        wins++;

        currentScore = (11 - attempts) * 10;

        if (currentScore > bestScore) {
            bestScore = currentScore;
        }

        resultLabel.setText("🎯 PERFECT GUESS!");
        resultLabel.setForeground(SUCCESS);

        hintLabel.setText(
                "You found the hidden number in "
                        + attempts
                        + " attempts!"
        );

        addHistory(
                "Round "
                        + roundNumber
                        + " — WIN — "
                        + currentScore
                        + " points — "
                        + attempts
                        + " attempts"
        );

        finishRound();
    }

    private void handleLoss() {
        losses++;
        currentScore = 0;

        resultLabel.setText("💥 ROUND OVER");
        resultLabel.setForeground(ERROR);

        hintLabel.setText(
                "The hidden number was "
                        + secretNumber
                        + "."
        );

        addHistory(
                "Round "
                        + roundNumber
                        + " — LOSS — 0 points — "
                        + attempts
                        + " attempts — Number: "
                        + secretNumber
        );

        finishRound();
    }

    private void finishRound() {
        guessField.setEnabled(false);
        guessButton.setEnabled(false);

        updateInformation();

        roundNumber++;
    }

    private void playAgain() {
        startNewRound();
    }

    private void changeDifficulty() {
        Difficulty selected =
                (Difficulty) difficultyBox.getSelectedItem();

        if (selected == null) {
            return;
        }

        currentDifficulty = selected;

        startNewRound();

        resultLabel.setText(
                selected.name + " mode started!"
        );

        resultLabel.setForeground(PRIMARY);
    }

    private void resetGame() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Reset all scores and game history?",
                "Reset Game",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            wins = 0;
            losses = 0;
            roundNumber = 1;
            currentScore = 0;
            bestScore = 0;

            historyArea.setText("");

            currentDifficulty =
                    (Difficulty) difficultyBox.getSelectedItem();

            startNewRound();

            resultLabel.setText("New game started!");
            resultLabel.setForeground(PRIMARY);
        }
    }

    private void updateInformation() {
        rangeLabel.setText(
                "Range: "
                        + currentDifficulty.minimum
                        + " - "
                        + currentDifficulty.maximum
                        + "  |  Max attempts: 10"
        );

        attemptLabel.setText(
                "Attempt: "
                        + attempts
                        + " / 10"
        );

        roundLabel.setText(
                "Current Round: "
                        + roundNumber
        );

        scoreLabel.setText(
                "Round Score: "
                        + currentScore
        );

        bestScoreLabel.setText(
                "Best Score: "
                        + bestScore
        );

        int completedRounds = wins + losses;

        double accuracy = completedRounds == 0
                ? 0
                : ((double) wins / completedRounds) * 100;

        accuracyLabel.setText(
                String.format(
                        "Accuracy: %.0f%%",
                        accuracy
                )
        );
    }

    private void showMessage(
            String message,
            Color color
    ) {
        resultLabel.setText(message);
        resultLabel.setForeground(color);
        hintLabel.setText("");
    }

    private void addHistory(String text) {
        if (historyArea.getText().isEmpty()) {
            historyArea.append(text);
        } else {
            historyArea.append("\n" + text);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            NGM game = new NGM();

            game.setVisible(true);
        });
    }
}