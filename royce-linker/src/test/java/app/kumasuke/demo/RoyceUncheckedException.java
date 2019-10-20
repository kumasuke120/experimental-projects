package app.kumasuke.demo;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.Royce;
import app.kumasuke.royce.except.UncheckedSQLException;

import java.sql.DriverManager;
import java.sql.SQLException;

public class RoyceUncheckedException {
    public static void main(String[] args) {
        ConnectionProvider provider = () -> {
            // needless with JDBC 4.0+ and Java SPI
            // Class.forName("<jdbc.driver-class>");
            return DriverManager.getConnection("<jdbc.url>", "<jdbc.user>", "<jdbc.password>");
        };

        Royce royce = Royce.nonTransactional(provider);
        try {
            royce.write(linker -> {
                // do actions that may throw SQLException
            });
        } catch (UncheckedSQLException e) {
            SQLException cause = e.getCause();
            cause.printStackTrace();
        }
    }
}
