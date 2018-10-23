package app.kumasuke.srs.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Config {
    public static final String CONF_SERVER_PORT = "server.port";
    public static final String CONF_DEFAULT_CHARSET = "server.defaultCharset";
    public static final String CONF_SERVER_HTTP_ROOT_DIRECTORY = "server.http.rootDirectory";
    public static final String CONF_SERVER_NAME = "server.name";

    private final Map<String, Object> config;

    public Config(Properties properties) {
        this.config = loadConfig(properties);
    }

    private Map<String, Object> loadConfig(Properties properties) {
        final var config = new HashMap<String, Object>();

        loadServerPort(config, properties);
        loadServerDefaultCharset(config, properties);
        loadServerRootDirectory(config, properties);
        loadServerName(config, properties);

        return Collections.unmodifiableMap(config);
    }

    private void loadServerPort(Map<String, Object> config, Properties properties) {
        try {
            final int port = Integer.parseInt(properties.getProperty(CONF_SERVER_PORT));
            config.put(CONF_SERVER_PORT, port);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot parse '" + CONF_SERVER_PORT + "'", e);
        }
    }

    private void loadServerDefaultCharset(Map<String, Object> config, Properties properties) {
        try {
            final Charset charset = Charset.forName(properties.getProperty(CONF_DEFAULT_CHARSET));
            config.put(CONF_DEFAULT_CHARSET, charset);
        } catch (UnsupportedCharsetException e) {
            throw new IllegalArgumentException("Cannot parse '" + CONF_DEFAULT_CHARSET + "'", e);
        }
    }

    private void loadServerRootDirectory(Map<String, Object> config, Properties properties) {
        try {
            final Path path = Paths.get(properties.getProperty(CONF_SERVER_HTTP_ROOT_DIRECTORY));
            if (Files.isDirectory(path)) {
                config.put(CONF_SERVER_HTTP_ROOT_DIRECTORY, path);
            } else {
                throw new IllegalArgumentException("'" + CONF_SERVER_HTTP_ROOT_DIRECTORY + "' is not a directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Cannot parse '" + CONF_SERVER_HTTP_ROOT_DIRECTORY + "'", e);
        }
    }

    private void loadServerName(Map<String, Object> config, Properties properties) {
        final String serverName = properties.getProperty(CONF_SERVER_NAME);
        if (serverName.matches(".+?/\\d\\.\\d")) {
            config.put(CONF_SERVER_NAME, serverName);
        } else {
            throw new IllegalArgumentException("The format of '" + CONF_SERVER_NAME + "' is not valid");
        }
    }

    public int getServerPort() {
        return (int) config.get(CONF_SERVER_PORT);
    }

    public Charset getServerDefaultCharset() {
        return (Charset) config.get(CONF_DEFAULT_CHARSET);
    }

    public Path getServerRootDirectory() {
        return (Path) config.get(CONF_SERVER_HTTP_ROOT_DIRECTORY);
    }

    public String getServerName() {
        return (String) config.get(CONF_SERVER_NAME);
    }

    public String getServerNameWithoutVersion() {
        final String serverName = getServerName();
        final int endPos = serverName.lastIndexOf("/");
        return serverName.substring(0, endPos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var config = (Config) o;
        return Objects.equals(this.config, config.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config);
    }
}
