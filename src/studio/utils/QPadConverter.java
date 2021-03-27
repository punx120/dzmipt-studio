package studio.utils;

import studio.core.Credentials;
import studio.core.DefaultAuthenticationMechanism;
import studio.kdb.Config;
import studio.kdb.Server;
import studio.kdb.ServerTreeNode;

import javax.swing.tree.TreeNode;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class QPadConverter {

    public static List<Server> importFromFiles(File file, ServerTreeNode root,
                                               String defaultAuth,
                                               Credentials defaultCredentials) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        List<Server> servers = new ArrayList<>();
        for(String line:lines) {
            Server server = convert(line, root, defaultAuth, defaultCredentials);
            if (server != null) servers.add(server);
        }
        return servers;
    }

    static Server convert(String line, String defaultAuth, Credentials defaultCredentials) {
        return convert(line, new ServerTreeNode(), defaultAuth, defaultCredentials);
    }

    private static Server convertConnection(String conn, String defaultAuth,
                                            Credentials defaultCredentials) {
        if (conn.startsWith(":")) conn = conn.substring(1);

        String[] nodes = conn.split(":");
        if (nodes.length<2) return null;

        String host = nodes[0];
        int port;
        try {
            port = Integer.parseInt(nodes[1]);
        } catch (NumberFormatException e) {
            return null;
        }
        String user = nodes.length>2 ? nodes[2] : "";
        String password = nodes.length>3 ? nodes[3] : "";
        String auth = "";
        if (password.contains("?")) {
            int index = password.indexOf('?');
            auth = password.substring(0, index);
        }

        if (user.equals("") && password.equals("")) {
            auth = defaultAuth;
            user = defaultCredentials.getUsername();
            password = defaultCredentials.getPassword();
        } else {
            if (auth.equals("")) {
                auth = DefaultAuthenticationMechanism.NAME;
            }
        }



        return new Server("", host, port, user, password, Config.getInstance().getDefaultBackgroundColor(), auth, false);
    }

    static Server convert(String line, ServerTreeNode root, String defaultAuth,
                            Credentials defaultCredentials) {
        if (! line.startsWith("`")) return null;
        line = line.substring(1);
        String[] items = line.split("\\`");
        if (items.length < 2) return null;

        Server server = convertConnection(items[0], defaultAuth, defaultCredentials);
        if (server == null) return null;

        server.setName(items[items.length-1]);

        TreeNode[] folderNodes = Stream.concat(
                    Stream.of(""),
                    Stream.of(items).skip(1).limit(items.length-2))
                        .map(ServerTreeNode::new).toArray(TreeNode[]:: new);
        ServerTreeNode folder = root.findPath(folderNodes, true);
        folder.add(server);
        server.setFolder(folder);

        return server;
    }
}
