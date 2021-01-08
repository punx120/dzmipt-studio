package studio.kdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.core.DefaultAuthenticationMechanism;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigTest {

    private Config config;
    private File tmpFile;
    private Server server;

    @BeforeEach
    public void init() throws IOException {
        tmpFile = File.createTempFile("studioforkdb", ".tmp");
        tmpFile.deleteOnExit();
        config = Config.getInstance(tmpFile.getPath());
        System.out.println("temp file " + tmpFile.getPath());

        server = new Server("testServer", "localhost",1111,
                "user", "pwd", Color.WHITE, DefaultAuthenticationMechanism.NAME, false);
        server.setFolder(config.getServerTree().add("testFolder"));
    }

    @Test
    public void testDifferentConfigs() throws IOException{
        Config config1 = config;
        init();

        int value = config1.getResultTabsCount();
        assertEquals(value, config.getResultTabsCount());

        config.setResultTabsCount(value+1);
        assertEquals(value+1, config.getResultTabsCount());
        assertEquals(value, config1.getResultTabsCount());

        assertEquals(value+1, Config.getInstance(tmpFile.getPath()).getResultTabsCount());
    }

    @Test void testLRUServer() {
        assertEquals(null, config.getLRUServer());

        config.addServer(server);
        config.setLRUServer(server);
        assertEquals(server, config.getLRUServer());
    }
}
