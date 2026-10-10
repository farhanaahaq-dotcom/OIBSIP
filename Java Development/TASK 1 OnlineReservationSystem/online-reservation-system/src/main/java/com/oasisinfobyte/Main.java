package com.oasisinfobyte;

import java.sql.SQLException;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            Database.initialize();
        } catch (SQLException exception) {
            System.err.println("Unable to initialize the reservation database.");
            exception.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
