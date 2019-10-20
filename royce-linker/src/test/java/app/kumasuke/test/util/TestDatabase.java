package app.kumasuke.test.util;

import app.kumasuke.royce.ConnectionProvider;
import app.kumasuke.royce.util.SingletonContext;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class TestDatabase implements ConnectionProvider {
    private static final String PROP_FILENAME = "/test-database.properties";

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    private static final SingletonContext<TestDatabase> singleton = new SingletonContext<>(TestDatabase::new);

    static {
        Properties prop = getDatabaseProperties();
        URL = prop.getProperty("database.url");
        USERNAME = prop.getProperty("database.username");
        PASSWORD = prop.getProperty("database.password");
    }

    private TestDatabase() {

    }

    public static TestDatabase getInstance() {
        return singleton.getInstance();
    }

    private static Properties getDatabaseProperties() {
        InputStream propIs = TestDatabase.class.getResourceAsStream(PROP_FILENAME);
        Properties prop = new Properties();
        try {
            prop.load(propIs);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return prop;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
