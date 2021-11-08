package studio.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import studio.kdb.Server;
import studio.ui.action.QueryExecutor;
import studio.utils.Content;
import studio.utils.LineEnding;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.io.File;

//@TODO: Do we really need both EditorTab and EditorPane?
public class EditorTab {

    private String title;
    private String filename;
    private Server server;
    private boolean modified = false;
    private LineEnding lineEnding = LineEnding.Unix;

    private static int scriptNumber = 0;

    private StudioPanel panel;
    private EditorPane editorPane;

    private static final Logger log = LogManager.getLogger();

    public EditorTab(StudioPanel panel) {
        this.panel = panel;
        init();
    }

    private void init() {
        if (editorPane != null) throw new IllegalStateException("The EditorTab has been already initialized");

        editorPane = new EditorPane(true);
        JTextComponent textArea = editorPane.getTextArea();

        textArea.putClientProperty(QueryExecutor.class, new QueryExecutor(this));
    }

    public void init(Content content) {
        try {
            RSyntaxTextArea textArea = getTextArea();
            textArea.getDocument().remove(0, textArea.getDocument().getLength());
            textArea.getDocument().insertString(0, content.getContent(),null);
            textArea.setCaretPosition(0);
            setModified(content.hasMixedLineEnding());
            setLineEnding(content.getLineEnding());
            textArea.discardAllEdits();
            StudioPanel.rebuildAll();
            textArea.requestFocus();
        }
        catch (BadLocationException e) {
            log.error("Unexpected exception", e);
        }

    }

    public StudioPanel getPanel() {
        return panel;
    }

    public void setPanel(StudioPanel panel) {
        this.panel = panel;
    }

    public EditorPane getPane() {
        return editorPane;
    }

    public RSyntaxTextArea getTextArea() {
        return editorPane.getTextArea();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
        if (filename == null) {
            title = "Script" + scriptNumber++;
        } else {
            title = new File(filename).getName();
        }
        panel.refreshTitle();
    }

    public String getTitle() {
        return title;
    }

    private String getTabTitleInternal() {
        if (getFilename() != null) return getTitle();
        Server server = getServer();
        if (server == null) return getTitle();

        if (server.getName().length() > 0) return server.getName();
        return server.getHost() + ":" + server.getPort();
    }

    public String getTabTitle() {
        String title = getTabTitleInternal();

        if (getFilename() == null) return title;
        if (isModified()) title = title + " *";
        return title;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean value) {
        if (modified == value) return;
        modified = value;
        panel.refreshTitle();
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
        getTextArea().setBackground(server.getBackgroundColor());
        if (server.equals(getServer())) return;

        setStatus("Changed server: " + server.getDescription(true));
    }

    public LineEnding getLineEnding() {
        return lineEnding;
    }

    public void setLineEnding(LineEnding lineEnding) {
        if (this.lineEnding == lineEnding) return;

        this.lineEnding = lineEnding;
        setModified(true);
    }

    public void setStatus(String status) {
        editorPane.setStatus(status);
    }

    public QueryExecutor getQueryExecutor() {
        return (QueryExecutor) getTextArea().getClientProperty(QueryExecutor.class);
    }
}
