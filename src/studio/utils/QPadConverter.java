package studio.utils;

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

    public static List<Server> importFromFiles(File file) throws IOException {
        ServerTreeNode root = new ServerTreeNode();
        List<String> lines = Files.readAllLines(file.toPath());
        List<Server> servers = new ArrayList<>();
        for(String line:lines) {
            Server server = convert(line, root);
            if (server != null) servers.add(server);
        }
        return servers;
    }

    static Server convert(String line) {
        return convert(line, new ServerTreeNode());
    }

    private static Server convertConnection(String conn) {
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
        String auth;
        if (password.contains("?")) {
            int index = password.indexOf('?');
            auth = password.substring(0, index);
            password = password.substring(index+1);
        } else {
            auth = DefaultAuthenticationMechanism.NAME;
        }

        return new Server("", host, port, user, password, Config.getInstance().getDefaultBackgroundColor(), auth, false);
    }

    static Server convert(String line, ServerTreeNode root) {
        if (! line.startsWith("`")) return null;
        line = line.substring(1);
        String[] items = line.split("\\`");
        if (items.length < 2) return null;

        Server server = convertConnection(items[0]);
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
