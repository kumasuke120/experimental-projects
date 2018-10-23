package app.kumasuke.test.srs.util;

import app.kumasuke.srs.util.Config;
import app.kumasuke.srs.util.ConfigUtil;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigUtilTest {
    @Test
    void getConfig() {
        final var expectedProp = new Properties();
        expectedProp.put(Config.CONF_SERVER_NAME, "K9MAServer/1.0");
        expectedProp.put(Config.CONF_SERVER_HTTP_ROOT_DIRECTORY, "D:\\public");
        expectedProp.put(Config.CONF_SERVER_PORT, "8888");
        expectedProp.put(Config.CONF_DEFAULT_CHARSET, "UTF-8");
        final Config expected = new Config(expectedProp);
        final Config actual = ConfigUtil.getConfig();

        assertEquals(expected, actual);
    }
}
