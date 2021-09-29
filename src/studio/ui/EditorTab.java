package studio.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.netbeans.editor.BaseDocument;
import studio.kdb.Server;
import studio.qeditor.RSToken;
import studio.qeditor.RSTokenMaker;
import studio.ui.action.QueryExecutor;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.io.File;

public class EditorTab {

    private static final String TITLE = "title";
    private static final String FILENAME = "filename";
    private static final String SERVER = "server";
    private static final String MODIFIED = "modified";

    private static int scriptNumber = 0;

    private StudioPanel panel;
    private EditorPane editorPane;

    private static final Logger log = LogManager.getLogger();

    public EditorTab(StudioPanel panel) {
        this.panel = panel;
    }

    public JComponent init() {
        if (editorPane != null) throw new IllegalStateException("The EditorTab has been already initialized");

        editorPane = new EditorPane();
        JTextComponent textArea = editorPane.getTextArea();

        textArea.putClientProperty(QueryExecutor.class, new QueryExecutor(this));
        Document doc = textArea.getDocument();
        doc.putProperty(MODIFIED, false);
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

        return editorPane;
    }

    public StudioPanel getPanel() {
        return panel;
    }

    public void setPanel(StudioPanel panel) {
        this.panel = panel;
    }

    public JTextComponent getTextArea() {
        return editorPane.getTextArea();
    }

    public String getFilename() {
        if (editorPane == null) return null;
        return (String) getTextArea().getDocument().getProperty(FILENAME);
    }

    public void setFilename(String filename) {
        getTextArea().getDocument().putProperty(FILENAME, filename);
        String title;
        if (filename == null) {
            title = "Script" + scriptNumber++;
        } else {
            title = new File(filename).getName();
        }
        getTextArea().getDocument().putProperty(TITLE, title);
        panel.refreshTitle();
    }

    public String getTitle() {
        if (editorPane == null) return null;
        return (String) getTextArea().getDocument().getProperty(TITLE);
    }

    public String getTabTitle() {
        if (getFilename() != null) return getTitle();
        Server server = getServer();
        if (server == null) return getTitle();

        if (server.getName().length() > 0) return server.getName();
        return server.getHost() + ":" + server.getPort();
    }

    public boolean isModified() {
        if (editorPane == null) return false;
        return (Boolean)getTextArea().getDocument().getProperty(MODIFIED);
    }

    public void setModified(boolean value) {
        getTextArea().getDocument().putProperty(MODIFIED, value);
        panel.refreshTitle();
    }

    public Server getServer() {
        if (editorPane == null) return null;
        return (Server)getTextArea().getDocument().getProperty(SERVER);
    }

    public void setServer(Server server) {
        if (server.equals(getServer())) return;

        getTextArea().getDocument().putProperty(SERVER,server);
        getTextArea().setBackground(server.getBackgroundColor());
        setStatus("Changed server: " + server.getDescription(true));
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
