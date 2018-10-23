package app.kumasuke.srs.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static final String CONFIG_FILE_NAME = "server-config.properties";

    public static Config getConfig() {
        try (final var is = loadConfig()) {
            final Properties properties = new Properties();
            properties.load(is);
            return new Config(properties);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load config file", e);
        }
    }

    private static InputStream loadConfig() {
        InputStream result = loadConfigFromWorkingDirectory();
        if (result == null) {
            result = loadConfigFromClassPath();
        }

        if (result == null) {
            throw new IllegalStateException("Cannot find config file with name '" + CONFIG_FILE_NAME + "'");
        } else {
            return result;
        }
    }

    private static InputStream loadConfigFromWorkingDirectory() {
        try {
            return new FileInputStream(CONFIG_FILE_NAME);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private static InputStream loadConfigFromClassPath() {
        InputStream result = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(CONFIG_FILE_NAME);

        if (result == null) {
            result = ClassLoader.getSystemResourceAsStream(CONFIG_FILE_NAME);
        }

        if (result == null) {
            result = ConfigUtil.class.getResourceAsStream("/" + CONFIG_FILE_NAME);
        }

        return result;
    }
}
