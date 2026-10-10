package com.oasisinfobyte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Looks up a reservation by PNR and allows the fetched reservation to be cancelled. */
public class CancellationFrame extends JFrame {
    private final DashboardFrame dashboardFrame;
    private final JTextField pnrField = new JTextField(18);
    private final JTextField pnrValue = createReadOnlyField();
    private final JTextField passengerNameValue = createReadOnlyField();
    private final JTextField trainNumberValue = createReadOnlyField();
    private final JTextField trainNameValue = createReadOnlyField();
    private final JTextField classValue = createReadOnlyField();
    private final JTextField journeyDateValue = createReadOnlyField();
    private final JTextField sourceValue = createReadOnlyField();
    private final JTextField destinationValue = createReadOnlyField();
    private final JButton confirmCancellationButton = new JButton("Confirm Cancellation");
    private String fetchedPnr;

    public CancellationFrame(DashboardFrame dashboardFrame) {
        this.dashboardFrame = dashboardFrame;
        setTitle("Cancel Reservation");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setContentPane(createContent());
        setSize(new Dimension(650, 540));
        setMinimumSize(new Dimension(620, 520));
        setLocationRelativeTo(null);

        confirmCancellationButton.setEnabled(false);
        pnrField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                clearFetchedReservation();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                clearFetchedReservation();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                clearFetchedReservation();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                returnToDashboard();
            }
        });
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(24, 34, 24, 34));

        JLabel title = new JLabel("Cancel Reservation", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(35, 57, 82));
        content.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);

        JPanel lookup = new JPanel(new GridBagLayout());
        lookup.setOpaque(false);
        GridBagConstraints lookupConstraints = new GridBagConstraints();
        lookupConstraints.insets = new Insets(5, 5, 5, 8);
        lookupConstraints.anchor = GridBagConstraints.WEST;
        lookupConstraints.gridx = 0;
        lookup.add(new JLabel("PNR Number:"), lookupConstraints);
        lookupConstraints.gridx = 1;
        lookupConstraints.weightx = 1;
        lookupConstraints.fill = GridBagConstraints.HORIZONTAL;
        lookup.add(pnrField, lookupConstraints);
        JButton fetchButton = new JButton("Fetch Reservation");
        fetchButton.addActionListener(event -> fetchReservation());
        lookupConstraints.gridx = 2;
        lookupConstraints.weightx = 0;
        lookupConstraints.fill = GridBagConstraints.NONE;
        lookup.add(fetchButton, lookupConstraints);
        pnrField.addActionListener(event -> fetchReservation());
        center.add(lookup, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridBagLayout());
        details.setBackground(Color.WHITE);
        details.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Booking Details"),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        GridBagConstraints detailConstraints = new GridBagConstraints();
        detailConstraints.insets = new Insets(5, 5, 5, 8);
        detailConstraints.anchor = GridBagConstraints.WEST;
        detailConstraints.fill = GridBagConstraints.HORIZONTAL;
        addDetailRow(details, detailConstraints, 0, "PNR", pnrValue);
        addDetailRow(details, detailConstraints, 1, "Passenger Name", passengerNameValue);
        addDetailRow(details, detailConstraints, 2, "Train Number", trainNumberValue);
        addDetailRow(details, detailConstraints, 3, "Train Name", trainNameValue);
        addDetailRow(details, detailConstraints, 4, "Class", classValue);
        addDetailRow(details, detailConstraints, 5, "Journey Date", journeyDateValue);
        addDetailRow(details, detailConstraints, 6, "Source", sourceValue);
        addDetailRow(details, detailConstraints, 7, "Destination", destinationValue);
        center.add(details, BorderLayout.CENTER);
        content.add(center, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        confirmCancellationButton.addActionListener(event -> confirmCancellation());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> returnToDashboard());
        actions.add(confirmCancellationButton);
        actions.add(backButton);
        content.add(actions, BorderLayout.SOUTH);
        return content;
    }

    private static void addDetailRow(JPanel panel, GridBagConstraints constraints,
            int row, String label, JTextField valueField) {
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.weightx = 0;
        panel.add(new JLabel(label + ":"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(valueField, constraints);
    }

    private static JTextField createReadOnlyField() {
        JTextField field = new JTextField(24);
        field.setEditable(false);
        field.setBackground(new Color(245, 247, 249));
        return field;
    }

    private void fetchReservation() {
        String pnr = pnrField.getText().trim();
        if (pnr.isEmpty()) {
            clearFetchedReservation();
            JOptionPane.showMessageDialog(this, "Please enter a PNR number.",
                    "PNR required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Reservation reservation = Database.findReservationByPnr(pnr);
            if (reservation == null) {
                clearFetchedReservation();
                JOptionPane.showMessageDialog(this, "Reservation not found for this PNR.",
                        "Reservation not found", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            fetchedPnr = reservation.getPnr();
            pnrValue.setText(reservation.getPnr());
            passengerNameValue.setText(reservation.getPassengerName());
            trainNumberValue.setText(reservation.getTrainNumber());
            trainNameValue.setText(reservation.getTrainName());
            classValue.setText(reservation.getClassType());
            journeyDateValue.setText(reservation.getJourneyDate());
            sourceValue.setText(reservation.getSource());
            destinationValue.setText(reservation.getDestination());
            confirmCancellationButton.setEnabled(true);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this,
                    "Unable to fetch the reservation. Please try again.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }

    private void confirmCancellation() {
        if (fetchedPnr == null) {
            confirmCancellationButton.setEnabled(false);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (Database.cancelReservation(fetchedPnr)) {
                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.",
                        "Cancellation complete", JOptionPane.INFORMATION_MESSAGE);
                pnrField.setText("");
                clearFetchedReservation();
            } else {
                clearFetchedReservation();
                JOptionPane.showMessageDialog(this, "Reservation not found for this PNR.",
                        "Reservation not found", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this,
                    "The reservation could not be cancelled. Please try again.",
                    "Cancellation error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }

    private void clearFetchedReservation() {
        fetchedPnr = null;
        pnrValue.setText("");
        passengerNameValue.setText("");
        trainNumberValue.setText("");
        trainNameValue.setText("");
        classValue.setText("");
        journeyDateValue.setText("");
        sourceValue.setText("");
        destinationValue.setText("");
        confirmCancellationButton.setEnabled(false);
    }

    private void returnToDashboard() {
        dashboardFrame.setVisible(true);
        dispose();
    }
}
