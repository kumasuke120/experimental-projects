package app.kumasuke.demo;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.Royce;

import java.sql.DriverManager;
import java.util.Collections;
import java.util.Map;

public class RoyceWrite {
    public static void main(String[] args) {
        ConnectionProvider provider = () -> {
            // needless with JDBC 4.0+ and Java SPI
            // Class.forName("<jdbc.driver-class>");
            return DriverManager.getConnection("<jdbc.url>", "<jdbc.user>", "<jdbc.password>");
        };

        Royce royce = Royce.transactional(provider);
        // all actions inside the lambda will form a transaction,
        // and it will be auto-committed at last or rollback-ed when
        // encountered any exception
        royce.write(linker -> {
            final String rollOut = "UPDATE `test`.`account` SET `balance` = `balance` - :amount WHERE `user_id` = 1";
            final String rollIn = "UPDATE `test`.`account` SET `balance` = `balance` + :amount WHERE `user_id` = 2";

            Map<String, Integer> amount = Collections.singletonMap("amount", 100);
            linker.namedUpdate(rollOut, amount);
            linker.namedUpdate(rollIn, amount);
        });
    }
}
