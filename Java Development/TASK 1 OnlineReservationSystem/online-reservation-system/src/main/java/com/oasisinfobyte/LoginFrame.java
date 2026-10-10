package com.oasisinfobyte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Login window for the online reservation system. */
public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = createLoginButton();

    public LoginFrame() {
        setTitle("Online Reservation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(createContent());
        getRootPane().setDefaultButton(loginButton);
        pack();
        setSize(new Dimension(460, 330));
        setMinimumSize(new Dimension(420, 330));
        setLocationRelativeTo(null);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(30, 38, 30, 38));

        JLabel title = new JLabel("Online Reservation System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(35, 57, 82));

        JLabel subtitle = new JLabel("Secure Ticket Reservation", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 112, 124));

        JPanel heading = new JPanel(new BorderLayout(0, 7));
        heading.setOpaque(false);
        heading.add(title, BorderLayout.CENTER);
        heading.add(subtitle, BorderLayout.SOUTH);
        content.add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(7, 4, 7, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        fields.add(new JLabel("Username"), constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        fields.add(usernameField, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        fields.add(new JLabel("Password"), constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        fields.add(passwordField, constraints);
        content.add(fields, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> clearFields());
        actions.add(loginButton);
        actions.add(clearButton);
        content.add(actions, BorderLayout.SOUTH);
        return content;
    }

    private JButton createLoginButton() {
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(event -> login());
        return loginButton;
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocusInWindow();
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (Database.authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(this,
                        "Login successful.", "Welcome", JOptionPane.INFORMATION_MESSAGE);
                new DashboardFrame(username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Access denied", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                passwordField.requestFocusInWindow();
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this,
                    "Unable to verify your login. Please try again.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }
}
