package app.kumasuke.demo;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.Royce;
import app.kumasuke.royce.mapper.Mappers;

import java.sql.DriverManager;

public class RoyceRead {
    public static void main(String[] args) {
        ConnectionProvider provider = () -> {
            // needless with JDBC 4.0+ and Java SPI
            // Class.forName("<jdbc.driver-class>");
            return DriverManager.getConnection("<jdbc.url>", "<jdbc.user>", "<jdbc.password>");
        };

        Royce royce = Royce.nonTransactional(provider);
        String message = royce.tryRead(linker -> {
            final String sql = "SELECT concat(?, ' ', ?)";
            return linker.selectOne(Mappers.firstColumnToString(), sql, "Hello", "World")
                    .orElse("") + "!";
        }).handle(e -> {
            e.printStackTrace();
            return "Hello Error...";
        });

        System.out.println(message);
    }
}
