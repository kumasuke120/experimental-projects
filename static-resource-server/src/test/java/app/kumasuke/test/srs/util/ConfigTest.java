package app.kumasuke.test.srs.util;

import app.kumasuke.srs.util.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {
    private static Config config;

    @BeforeAll
    static void initAll() {
        final var expectedProp = new Properties();
        expectedProp.put(Config.CONF_SERVER_NAME, "K9MAServer/1.0");
        expectedProp.put(Config.CONF_SERVER_HTTP_ROOT_DIRECTORY, "D:\\public");
        expectedProp.put(Config.CONF_SERVER_PORT, "8888");
        expectedProp.put(Config.CONF_DEFAULT_CHARSET, "UTF-8");
        config = new Config(expectedProp);
    }

    @Test
    void getServerPort() {
        assertEquals(8888, config.getServerPort());
    }

    @Test
    void getServerDefaultCharset() {
        assertEquals(Charset.forName("UTF-8"), config.getServerDefaultCharset());
    }

    @Test
    void getServerRootDirectory() {
        assertEquals(Paths.get("D:\\public"), config.getServerRootDirectory());
    }

    @Test
    void getServerName() {
        assertEquals("K9MAServer/1.0", config.getServerName());
    }

    @Test
    void getServerNameWithoutVersion() {
        assertEquals("K9MAServer", config.getServerNameWithoutVersion());
    }
}
