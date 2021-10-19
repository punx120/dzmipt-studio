package studio.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.netbeans.editor.BaseDocument;
import studio.kdb.Server;
import studio.ui.action.QueryExecutor;
import studio.utils.LineEnding;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
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

        editorPane = new EditorPane();
        JTextComponent textArea = editorPane.getTextArea();

        textArea.putClientProperty(QueryExecutor.class, new QueryExecutor(this));
        Document doc = textArea.getDocument();
        UndoManager um = new UndoManager() {
            public void undoableEditHappened(UndoableEditEvent e) {
                super.undoableEditHappened(e);
                panel.refreshActionState();
            }

            public synchronized void redo() throws CannotRedoException {
                super.redo();
                panel.refreshActionState();
            }

            public synchronized void undo() throws CannotUndoException {
                super.undo();
                panel.refreshActionState();
            }
        };
        doc.putProperty(BaseDocument.UNDO_MANAGER_PROP,um);
        doc.addUndoableEditListener(um);
    }

    public StudioPanel getPanel() {
        return panel;
    }

    public void setPanel(StudioPanel panel) {
        this.panel = panel;
    }

    public JComponent getPane() {
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
        this.lineEnding = lineEnding;
    }

    public void setStatus(String status) {
        editorPane.setStatus(status);
    }

    public QueryExecutor getQueryExecutor() {
        return (QueryExecutor) getTextArea().getClientProperty(QueryExecutor.class);
    }

    public UndoManager getUndoManager() {
        return (UndoManager) getTextArea().getDocument().getProperty(BaseDocument.UNDO_MANAGER_PROP);
    }

}
