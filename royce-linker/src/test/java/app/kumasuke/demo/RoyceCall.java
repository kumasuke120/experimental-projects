package app.kumasuke.demo;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.Royce;
import app.kumasuke.royce.util.CallParameter;

import java.sql.DriverManager;
import java.sql.SQLException;

public class RoyceCall {
    public static void main(String[] args) {
        ConnectionProvider provider = () -> {
            // needless with JDBC 4.0+ and Java SPI
            // Class.forName("<jdbc.driver-class>");
            return DriverManager.getConnection("<jdbc.url>", "<jdbc.user>", "<jdbc.password>");
        };

        Royce royce = Royce.nonTransactional(provider);
        royce.tryCall(linker -> {
            // assume there is a procedure:
            // CREATE PROCEDURE test.`do_procedure`(
            //  IN  a   INT,
            //  IN  b   INT,
            //  OUT sum INT
            // )
            CallParameter<Integer> a = CallParameter.in(1);
            CallParameter<Integer> b = CallParameter.in(2);
            CallParameter<Integer> sum = CallParameter.out();
            linker.call("{call test.`do_procedure` (?, ?, ?)}", a, b, sum);

            int resultSum = sum.getValue();
            System.out.println(resultSum);
        }).handle(SQLException::printStackTrace);
    }
}
