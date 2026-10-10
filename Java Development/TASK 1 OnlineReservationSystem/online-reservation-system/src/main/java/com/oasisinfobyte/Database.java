package com.oasisinfobyte;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/** Initializes the SQLite database and its tables. */
public final class Database {
    private static final String DATABASE_URL = "jdbc:sqlite:online_reservation.db";

    private Database() {
        // Utility class
    }

    /** Creates the database file and required tables if they do not exist. */
    public static void initialize() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT NOT NULL UNIQUE, "
                    + "password TEXT NOT NULL"
                    + ")");

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS trains ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "train_number TEXT NOT NULL UNIQUE, "
                    + "train_name TEXT NOT NULL, "
                    + "source TEXT NOT NULL, "
                    + "destination TEXT NOT NULL"
                    + ")");

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS reservations ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "pnr TEXT NOT NULL UNIQUE, "
                    + "passenger_name TEXT NOT NULL, "
                    + "train_number TEXT NOT NULL, "
                    + "train_name TEXT NOT NULL, "
                    + "class_type TEXT NOT NULL, "
                    + "journey_date TEXT NOT NULL, "
                    + "source TEXT NOT NULL, "
                    + "destination TEXT NOT NULL, "
                    + "created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP"
                    + ")");
        }

        seedDefaultUser();
        seedSampleTrains();
    }

    /** Checks the supplied credentials against the users table. */
    public static boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    /** Returns train number, name, source, and destination, or null when absent. */
    public static String[] findTrainByNumber(String trainNumber) throws SQLException {
        String sql = "SELECT train_number, train_name, source, destination "
                + "FROM trains WHERE train_number = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, trainNumber);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new String[] {
                            result.getString("train_number"),
                            result.getString("train_name"),
                            result.getString("source"),
                            result.getString("destination")
                    };
                }
                return null;
            }
        }
    }

    /** Saves a reservation and returns it with its generated ID and PNR. */
    public static Reservation createReservation(Reservation reservation) throws SQLException {
        String insertSql = "INSERT INTO reservations "
                + "(pnr, passenger_name, train_number, train_name, class_type, "
                + "journey_date, source, destination) "
                + "SELECT 'PNR' || printf('%08d', COALESCE((SELECT seq "
                + "FROM sqlite_sequence WHERE name = 'reservations'), 0) + 1), "
                + "?, ?, ?, ?, ?, ?, ?";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            connection.setAutoCommit(false);
            try {
                long reservationId;
                try (PreparedStatement insert = connection.prepareStatement(
                        insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insert.setString(1, reservation.getPassengerName());
                    insert.setString(2, reservation.getTrainNumber());
                    insert.setString(3, reservation.getTrainName());
                    insert.setString(4, reservation.getClassType());
                    insert.setString(5, reservation.getJourneyDate());
                    insert.setString(6, reservation.getSource());
                    insert.setString(7, reservation.getDestination());
                    if (insert.executeUpdate() != 1) {
                        throw new SQLException("Reservation was not inserted.");
                    }
                    try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                        if (!generatedKeys.next()) {
                            throw new SQLException("Database did not return a reservation ID.");
                        }
                        reservationId = generatedKeys.getLong(1);
                    }
                }

                String pnr = String.format(Locale.ROOT, "PNR%08d", reservationId);
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE reservations SET pnr = ? WHERE id = ?")) {
                    update.setString(1, pnr);
                    update.setLong(2, reservationId);
                    if (update.executeUpdate() != 1) {
                        throw new SQLException("Reservation PNR could not be saved.");
                    }
                }

                connection.commit();
                Reservation savedReservation = new Reservation(
                        reservation.getPassengerName(), reservation.getTrainNumber(),
                        reservation.getTrainName(), reservation.getClassType(),
                        reservation.getJourneyDate(), reservation.getSource(),
                        reservation.getDestination());
                savedReservation.setId(reservationId);
                savedReservation.setPnr(pnr);
                return savedReservation;
            } catch (SQLException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        }
    }

    /** Finds a saved reservation by its PNR, or returns null when it is absent. */
    public static Reservation findReservationByPnr(String pnr) throws SQLException {
        String sql = "SELECT id, pnr, passenger_name, train_number, train_name, class_type, "
                + "journey_date, source, destination FROM reservations WHERE pnr = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pnr);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                Reservation reservation = new Reservation(
                        result.getString("passenger_name"),
                        result.getString("train_number"),
                        result.getString("train_name"),
                        result.getString("class_type"),
                        result.getString("journey_date"),
                        result.getString("source"),
                        result.getString("destination"));
                reservation.setId(result.getLong("id"));
                reservation.setPnr(result.getString("pnr"));
                return reservation;
            }
        }
    }

    /** Deletes only the reservation matching the supplied PNR. */
    public static boolean cancelReservation(String pnr) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM reservations WHERE pnr = ?")) {
            statement.setString(1, pnr);
            return statement.executeUpdate() == 1;
        }
    }

    private static void seedDefaultUser() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            boolean userExists;
            try (PreparedStatement check = connection.prepareStatement(
                    "SELECT 1 FROM users WHERE username = ?")) {
                check.setString(1, "admin");
                try (ResultSet result = check.executeQuery()) {
                    userExists = result.next();
                }
            }

            if (!userExists) {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO users (username, password) VALUES (?, ?)")) {
                    insert.setString(1, "admin");
                    insert.setString(2, "admin123");
                    insert.executeUpdate();
                }
            }
        }
    }

    private static void seedSampleTrains() throws SQLException {
        String[][] trains = {
                {"12601", "Chennai Express", "Chennai", "Mumbai"},
                {"12602", "Tamil Nadu Express", "Chennai", "New Delhi"},
                {"12603", "Kaveri Express", "Chennai", "Bengaluru"},
                {"12604", "Vaigai Express", "Chennai", "Madurai"}
        };

        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            for (String[] train : trains) {
                boolean trainExists;
                try (PreparedStatement check = connection.prepareStatement(
                        "SELECT 1 FROM trains WHERE train_number = ?")) {
                    check.setString(1, train[0]);
                    try (ResultSet result = check.executeQuery()) {
                        trainExists = result.next();
                    }
                }

                if (!trainExists) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO trains "
                            + "(train_number, train_name, source, destination) "
                            + "VALUES (?, ?, ?, ?)")) {
                        insert.setString(1, train[0]);
                        insert.setString(2, train[1]);
                        insert.setString(3, train[2]);
                        insert.setString(4, train[3]);
                        insert.executeUpdate();
                    }
                }
            }
        }
    }
}
