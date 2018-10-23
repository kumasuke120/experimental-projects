package app.kumasuke.test;

import app.kumasuke.srs.Server;
import app.kumasuke.srs.protocol.HttpProtocolFactory;
import app.kumasuke.srs.protocol.ProtocolFactory;
import app.kumasuke.srs.util.Config;
import app.kumasuke.srs.util.ConfigUtil;

public class Main {
    public static void main(String[] args) {
        final Config config = ConfigUtil.getConfig();
        final ProtocolFactory protocolSupport = new HttpProtocolFactory();
        final var server = new Server(config, protocolSupport);

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}