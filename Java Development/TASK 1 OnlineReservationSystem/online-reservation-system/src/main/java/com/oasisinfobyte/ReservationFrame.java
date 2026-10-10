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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Collects, validates, and saves reservation details. */
public class ReservationFrame extends JFrame {
    private static final DateTimeFormatter JOURNEY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final DashboardFrame dashboardFrame;
    private final JTextField passengerNameField = new JTextField(22);
    private final JTextField trainNumberField = new JTextField(22);
    private final JTextField trainNameField = createReadOnlyField();
    private final JComboBox<String> classTypeBox = new JComboBox<String>(new String[] {
            "Sleeper", "AC 3 Tier", "AC 2 Tier", "First Class"
    });
    private final JTextField journeyDateField = new JTextField(22);
    private final JTextField sourceField = createReadOnlyField();
    private final JTextField destinationField = createReadOnlyField();
    private String lastNotFoundTrainNumber;

    public ReservationFrame(DashboardFrame dashboardFrame) {
        this.dashboardFrame = dashboardFrame;
        setTitle("Online Reservation System - Book Ticket");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setContentPane(createContent());
        pack();
        setSize(new Dimension(560, 510));
        setMinimumSize(new Dimension(540, 490));
        setLocationRelativeTo(null);

        trainNumberField.addActionListener(event -> lookupTrain(true));
        trainNumberField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                lookupTrain(true);
            }
        });
        trainNumberField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                trainNumberChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                trainNumberChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                trainNumberChanged();
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
        content.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        JLabel heading = new JLabel("Book a Ticket", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(new Color(35, 57, 82));
        content.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 5, 8, 5);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0;

        addRow(form, constraints, 0, "Passenger Name", passengerNameField);
        addRow(form, constraints, 1, "Train Number", trainNumberField);
        addRow(form, constraints, 2, "Train Name", trainNameField);
        addRow(form, constraints, 3, "Class Type", classTypeBox);
        addRow(form, constraints, 4, "Journey Date (dd-MM-yyyy)", journeyDateField);
        addRow(form, constraints, 5, "Source Station", sourceField);
        addRow(form, constraints, 6, "Destination Station", destinationField);
        classTypeBox.setSelectedIndex(-1);
        content.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        JButton bookButton = new JButton("Book Ticket");
        bookButton.addActionListener(event -> validateAndConfirm());
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(event -> clearForm());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> returnToDashboard());
        actions.add(bookButton);
        actions.add(clearButton);
        actions.add(backButton);
        content.add(actions, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(bookButton);
        return content;
    }

    private static void addRow(JPanel panel, GridBagConstraints constraints,
            int row, String labelText, java.awt.Component field) {
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(labelText), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, constraints);
    }

    private static JTextField createReadOnlyField() {
        JTextField field = new JTextField(22);
        field.setEditable(false);
        field.setBackground(new Color(245, 247, 249));
        return field;
    }

    private void trainNumberChanged() {
        lastNotFoundTrainNumber = null;
        trainNameField.setText("");
        sourceField.setText("");
        destinationField.setText("");
    }

    private boolean lookupTrain(boolean showMissingMessage) {
        String trainNumber = trainNumberField.getText().trim();
        if (trainNumber.isEmpty()) {
            return false;
        }

        try {
            String[] train = Database.findTrainByNumber(trainNumber);
            if (train == null) {
                if (showMissingMessage && !trainNumber.equals(lastNotFoundTrainNumber)) {
                    lastNotFoundTrainNumber = trainNumber;
                    JOptionPane.showMessageDialog(this, "Train number not found.",
                            "Train not found", JOptionPane.WARNING_MESSAGE);
                }
                return false;
            }

            trainNameField.setText(train[1]);
            sourceField.setText(train[2]);
            destinationField.setText(train[3]);
            lastNotFoundTrainNumber = null;
            return true;
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Unable to look up the train right now.",
                    "Database error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
            return false;
        }
    }

    private void validateAndConfirm() {
        String passengerName = passengerNameField.getText().trim();
        String trainNumber = trainNumberField.getText().trim();
        if (passengerName.isEmpty()) {
            showValidationError("Passenger name is required.");
            passengerNameField.requestFocusInWindow();
            return;
        }
        if (trainNumber.isEmpty()) {
            showValidationError("Train number is required.");
            trainNumberField.requestFocusInWindow();
            return;
        }
        if (!trainNumber.matches("[0-9]+")) {
            showValidationError("Train number must be numeric.");
            trainNumberField.requestFocusInWindow();
            return;
        }
        String[] train;
        try {
            train = Database.findTrainByNumber(trainNumber);
        } catch (SQLException exception) {
            showValidationError("Unable to verify the selected train. Please try again.");
            exception.printStackTrace();
            return;
        }
        if (train == null) {
            trainNameField.setText("");
            sourceField.setText("");
            destinationField.setText("");
            showValidationError("Train number not found.");
            return;
        }
        trainNameField.setText(train[1]);
        sourceField.setText(train[2]);
        destinationField.setText(train[3]);

        Object selectedClass = classTypeBox.getSelectedItem();
        if (selectedClass == null) {
            showValidationError("Please select a class type.");
            classTypeBox.requestFocusInWindow();
            return;
        }

        String journeyDate = journeyDateField.getText().trim();
        if (journeyDate.isEmpty()) {
            showValidationError("Journey date is required.");
            journeyDateField.requestFocusInWindow();
            return;
        }
        try {
            LocalDate.parse(journeyDate, JOURNEY_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            showValidationError("Enter a valid journey date in dd-MM-yyyy format.");
            journeyDateField.requestFocusInWindow();
            return;
        }

        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();
        if (source.isEmpty() || destination.isEmpty()) {
            showValidationError("Train source and destination are required.");
            return;
        }
        if (source.equalsIgnoreCase(destination)) {
            showValidationError("Source and destination cannot be the same.");
            return;
        }

        Reservation reservation = new Reservation(passengerName, trainNumber,
                train[1], selectedClass.toString(), journeyDate, train[2], train[3]);
        try {
            Reservation savedReservation = Database.createReservation(reservation);
            String confirmation = "Reservation Confirmed!\n\n"
                    + "PNR: " + savedReservation.getPnr() + "\n"
                    + "Passenger Name: " + savedReservation.getPassengerName() + "\n"
                    + "Train Number: " + savedReservation.getTrainNumber() + "\n"
                    + "Train Name: " + savedReservation.getTrainName() + "\n"
                    + "Class: " + savedReservation.getClassType() + "\n"
                    + "Journey Date: " + savedReservation.getJourneyDate() + "\n"
                    + "From: " + savedReservation.getSource() + "\n"
                    + "To: " + savedReservation.getDestination();
            JOptionPane.showMessageDialog(this, confirmation,
                    "Reservation Confirmed", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this,
                    "The reservation could not be saved. Please try again.",
                    "Booking error", JOptionPane.ERROR_MESSAGE);
            exception.printStackTrace();
        }
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Please check the form", JOptionPane.WARNING_MESSAGE);
    }

    private void clearForm() {
        passengerNameField.setText("");
        trainNumberField.setText("");
        trainNameField.setText("");
        classTypeBox.setSelectedIndex(-1);
        journeyDateField.setText("");
        sourceField.setText("");
        destinationField.setText("");
        lastNotFoundTrainNumber = null;
        passengerNameField.requestFocusInWindow();
    }

    private void returnToDashboard() {
        dashboardFrame.setVisible(true);
        dispose();
    }
}
