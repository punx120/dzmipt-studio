package studio.ui.action;

import org.junit.jupiter.api.Test;
import studio.core.DefaultAuthenticationMechanism;
import studio.kdb.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QPadImporterTest {

    @Test
    public void testConvert() {
        Server server = QPadImporter.convert("`:server.uk.db.com:11223`name");
        assertEquals("server.uk.db.com", server.getHost());
        assertEquals(11223, server.getPort());
        assertEquals("name", server.getName());
        assertEquals(DefaultAuthenticationMechanism.NAME, server.getAuthenticationMechanism());

        server = QPadImporter.convert("`:server.uk.db.com:11223`Parent`folder`name");
        assertEquals("Parent/folder/name", server.getFullName());
        assertEquals("name", server.getName());

        server = QPadImporter.convert("`:server.uk.db.com:11223:user:password`Parent`folder`name");
        assertEquals("Parent/folder/name", server.getFullName());
        assertEquals("user", server.getUsername());
        assertEquals("password", server.getPassword());

        server = QPadImporter.convert("`:server.uk.db.com:11223:user:auth?password`Parent`folder`name");
        assertEquals("Parent/folder/name", server.getFullName());
        assertEquals("user", server.getUsername());
        assertEquals("password", server.getPassword());
        assertEquals("auth", server.getAuthenticationMechanism());


        server = QPadImporter.convert("#`:server.uk.db.com:11223:user:auth?password`Parent`folder`name");
        assertEquals(null, server);

        server = QPadImporter.convert("");
        assertEquals(null, server);

        server = QPadImporter.convert("`:server.uk.db.com:11223");
        assertEquals(null, server);

        server = QPadImporter.convert("server.uk.db.com:11223`root`folder`somename");
        assertEquals(null, server);

        server = QPadImporter.convert("`:server.uk.db.com:port`name");
        assertEquals(null, server);

        server = QPadImporter.convert("`:server.uk.db.com`name");
        assertEquals(null, server);

        server = QPadImporter.convert("`:server.uk.db.com:11223:user`name");
        assertEquals("", server.getPassword());
        assertEquals("user", server.getUsername());
    }

}
