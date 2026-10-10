package com.oasisinfobyte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Temporary landing window shown after a successful login. */
public class DashboardFrame extends JFrame {
    private ReservationFrame reservationFrame;
    private CancellationFrame cancellationFrame;

    public DashboardFrame(String username) {
        setTitle("Online Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel content = new JPanel(new BorderLayout(0, 24));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        JLabel welcome = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcome.setForeground(new Color(35, 57, 82));
        content.add(welcome, BorderLayout.NORTH);

        JLabel note = new JLabel("Choose an option to continue.",
                SwingConstants.CENTER);
        note.setFont(new Font("SansSerif", Font.PLAIN, 14));
        content.add(note, BorderLayout.CENTER);

        JPanel placeholders = new JPanel(new GridLayout(1, 2, 12, 0));
        placeholders.setOpaque(false);
        JButton bookButton = new JButton("Book Ticket");
        JButton cancelButton = new JButton("Cancel Ticket");
        bookButton.addActionListener(event -> openReservationForm());
        cancelButton.addActionListener(event -> openCancellationForm());
        placeholders.add(bookButton);
        placeholders.add(cancelButton);
        content.add(placeholders, BorderLayout.SOUTH);

        setContentPane(content);
        setPreferredSize(new Dimension(560, 250));
        pack();
        setLocationRelativeTo(null);
    }

    private void openReservationForm() {
        if (reservationFrame == null || !reservationFrame.isDisplayable()) {
            reservationFrame = new ReservationFrame(this);
        }
        setVisible(false);
        reservationFrame.setVisible(true);
    }

    private void openCancellationForm() {
        if (cancellationFrame == null || !cancellationFrame.isDisplayable()) {
            cancellationFrame = new CancellationFrame(this);
        }
        setVisible(false);
        cancellationFrame.setVisible(true);
    }
}
