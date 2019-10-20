package app.kumasuke.demo;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.Royce;

import java.sql.DriverManager;
import java.sql.SQLException;

public class RoyceMaybeException {
    public static void main(String[] args) {
        ConnectionProvider provider = () -> {
            // needless with JDBC 4.0+ and Java SPI
            // Class.forName("<jdbc.driver-class>");
            return DriverManager.getConnection("<jdbc.url>", "<jdbc.user>", "<jdbc.password>");
        };

        Royce royce = Royce.nonTransactional(provider);
        // do something with e ...
        royce.tryWrite(linker -> {
            // do actions that may throw SQLException
        }).handle(SQLException::printStackTrace);
    }
}
