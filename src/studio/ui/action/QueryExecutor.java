package studio.ui.action;

import org.netbeans.editor.Utilities;
import studio.kdb.*;
import studio.ui.StudioPanel;
import studio.utils.SwingWorker;

import javax.swing.*;
import java.awt.*;

public class QueryExecutor {

    private SwingWorker worker;
    private JEditorPane textArea;
    private Cursor defaultCursor;
    private Server server;


    public void cancel() {
        if (worker != null) {
            worker.interrupt();
            textArea.setCursor(defaultCursor);
        }
        worker = null;
    }

    public void execute(StudioPanel panel, String query) {
        textArea = panel.getTextArea();
        server = panel.getServer();
        defaultCursor = textArea.getCursor();
        textArea.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));


        worker = new SwingWorker() {
            Server s = null;
            kx.c c = null;
            K.KBase r = null;
            Throwable exception;
            boolean cancelled = false;
            long execTime=0;
            public void interrupt() {
                super.interrupt();

                cancelled = true;

                if (c != null)
                    c.close();
                cleanup();
            }

            public Object construct() {
                long startTime=System.currentTimeMillis();
                try {
                    this.s = server;
                    c = ConnectionPool.getInstance().leaseConnection(s);
                    ConnectionPool.getInstance().checkConnected(c);
                    c.setParent(textArea);
                    c.k(new K.KCharacterVector(query));
                    r = c.getResponse();
                }
                catch (Throwable e) {
                    System.err.println("Error occurred during query execution: " + e);
                    e.printStackTrace(System.err);
                    exception = e;
                }
                execTime=System.currentTimeMillis()-startTime;
                return null;
            }

            public void finished() {
                if (!cancelled) {
                    panel.queryExecutionComplete(r, exception);
                    Utilities.setStatusText(textArea, "Last execution time:"+(execTime>0?""+execTime:"<1")+" mS");
                    if (c != null)
                        ConnectionPool.getInstance().freeConnection(s,c);
                    //if( c != null)
                    //    c.close();
                    c = null;

                    textArea.setCursor(defaultCursor);
                    System.gc();
                    worker = null;
                }
            }

            private void cleanup() {
            }
        };

        worker.start();

    }

}
