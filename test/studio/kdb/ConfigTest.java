package studio.kdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.core.DefaultAuthenticationMechanism;

import java.awt.*;
import java.io.*;
import java.util.Properties;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void addServerDifferentTreeNode() {
        Server server1 = new Server("testServer1", "localhost",1112,
                "user", "pwd", Color.WHITE, DefaultAuthenticationMechanism.NAME, false);
        server1.setFolder(new ServerTreeNode().add("addServerTestFolder"));

        Server server2 = new Server("testServer2", "localhost",1113,
                "user", "pwd", Color.WHITE, DefaultAuthenticationMechanism.NAME, false);
        server2.setFolder(new ServerTreeNode().add("addServerTestFolder"));

        config.addServers(server1, server2);
        assertEquals(2, config.getServerNames().size());
        assertEquals(2, config.getServerTree().getChild("addServerTestFolder").getChildCount() );
    }

    @Test
    public void addServerSameName() {
        Server server1 = new Server("testServer1", "localhost",1112,
                "user", "pwd", Color.WHITE, DefaultAuthenticationMechanism.NAME, false);
        server1.setFolder(config.getServerTree().add("sameNameTestFolder"));

        Server server2 = new Server("testServer1", "localhost",1113,
                "user", "pwd", Color.WHITE, DefaultAuthenticationMechanism.NAME, false);
        server2.setFolder(config.getServerTree().add("sameNameTestFolder"));

        config.addServers(server1);
        assertThrows(IllegalArgumentException.class, ()->config.addServer(server2) );

        assertEquals(1, config.getServerNames().size());
        assertEquals(1, config.getServerTree().getChild("sameNameTestFolder").getChildCount() );
        assertEquals(server1.getPort(), config.getServer("sameNameTestFolder/testServer1").getPort());
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

    @Test
    public void testServerHistoryDepth() {
        int depth = config.getServerHistoryDepth();
        config.setServerHistoryDepth(depth+1);
        assertEquals(depth+1, config.getServerHistoryDepth());
    }

    @Test
    public void testServerHistory() {
        assertEquals(0, config.getServerHistory().size());

        config.addServer(server);
        assertEquals(0, config.getServerHistory().size());
        config.addServerToHistory(server);
        assertEquals(1, config.getServerHistory().size());
        assertEquals(server, config.getServerHistory().get(0));
        config.addServerToHistory(server);
        assertEquals(1, config.getServerHistory().size());


        Server server1 = new Server(server);
        server1.setName("testServer1");
        Server server2 = new Server(server);
        server2.setName("testServer2");
        Server server3 = new Server(server);
        server3.setName("testServer3");

        config.addServers(server1, server2, server3);
        config.addServerToHistory(server1);
        config.addServerToHistory(server2);
        assertEquals(3, config.getServerHistory().size());
        assertEquals(server2, config.getServerHistory().get(0));
        assertEquals(server1, config.getServerHistory().get(1));
        assertEquals(server, config.getServerHistory().get(2));

        config.addServerToHistory(server1);
        assertEquals(3, config.getServerHistory().size());
        assertEquals(server1, config.getServerHistory().get(0));
        assertEquals(server2, config.getServerHistory().get(1));
        assertEquals(server, config.getServerHistory().get(2));

        config.addServerToHistory(server);
        assertEquals(3, config.getServerHistory().size());
        assertEquals(server, config.getServerHistory().get(0));
        assertEquals(server1, config.getServerHistory().get(1));
        assertEquals(server2, config.getServerHistory().get(2));

        config.addServerToHistory(server);
        assertEquals(3, config.getServerHistory().size());
        assertEquals(server, config.getServerHistory().get(0));
        assertEquals(server1, config.getServerHistory().get(1));
        assertEquals(server2, config.getServerHistory().get(2));

        config.setServerHistoryDepth(3);
        config.addServerToHistory(server3);
        assertEquals(3, config.getServerHistory().size());
        assertEquals(server3, config.getServerHistory().get(0));
        assertEquals(server, config.getServerHistory().get(1));
        assertEquals(server1, config.getServerHistory().get(2));
    }

    private Config copyConfigFromFile(File file, Consumer<Properties> propsModification) throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(file));
        propsModification.accept(p);

        File newFile = File.createTempFile("studioforkdb", ".tmp");
        newFile.deleteOnExit();
        OutputStream out = new FileOutputStream(newFile);
        p.store(out, null);
        out.close();

        return Config.getInstance(newFile.getPath());
    }

    @Test
    public void upgrade13Test() throws IOException {
        config.addServer(server);

        Config newConfig = copyConfigFromFile(tmpFile, p -> {
            p.setProperty("version", "1.2");
        });
        assertEquals(0, newConfig.getServerHistory().size());

        newConfig = copyConfigFromFile(tmpFile, p -> {
            p.setProperty("version", "1.2");
            p.setProperty("lruServer", server.getFullName());
        });
        assertEquals(1, newConfig.getServerHistory().size());
        assertEquals(server, newConfig.getServerHistory().get(0));
    }
}
