package studio.ui.action;

import kx.ProgressCallback;
import studio.kdb.*;
import studio.ui.StudioPanel;

import javax.swing.*;

public class QueryExecutor implements ProgressCallback {

    private Worker worker = null;
    private final StudioPanel studioPanel;

    private volatile ProgressMonitor pm;
    private boolean compressed;
    private int msgLength;

    public QueryExecutor(StudioPanel studioPanel) {
        this.studioPanel = studioPanel;
    }

    public void execute(String query) {
        worker = new Worker(studioPanel.getServer(), query);
        worker.execute();
    }

    public void cancel() {
        if (worker == null) return;
        if (worker.isDone()) return;
        worker.closeConnection();
        worker.cancel(true);
        worker = null;
    }

    @Override
    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    @Override
    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;

        final String message = "Receiving " + (compressed ? "compressed " : "") + "data ...";
        final String note = "0 of " + (msgLength / 1024) + " kB";
        final String title = "Studio for kdb+";
        UIManager.put("ProgressMonitor.progressText", title);
        pm = new ProgressMonitor(studioPanel, message, note, 0, msgLength);
        SwingUtilities.invokeLater( () -> {
            pm.setMillisToDecideToPopup(300);
            pm.setMillisToPopup(100);
            pm.setProgress(0);
        });
    }

    @Override
    public void setCurrentProgress(int total) {
        SwingUtilities.invokeLater( () -> {
            pm.setProgress(total);
            pm.setNote((total / 1024) + " of " + (msgLength / 1024) + " kB");
        });

        if (pm.isCanceled()) {
            cancel();
        }
    }

    private class Worker extends SwingWorker<QueryResult, Integer> {

        private volatile Server server;
        private volatile String query;
        private volatile kx.c c = null;

        public Worker (Server server, String query) {
            this.server = server;
            this.query = query;
        }

        void closeConnection() {
            if (c!=null) {
                c.close();
            }
        }

        @Override
        protected QueryResult doInBackground() {
            QueryResult result = new QueryResult(server, query);
            long startTime = System.currentTimeMillis();
            try {
                c = ConnectionPool.getInstance().leaseConnection(server);
                ConnectionPool.getInstance().checkConnected(c);
                K.KBase response = c.k(new K.KCharacterVector(query), QueryExecutor.this);
                result.setResult(response);
            } catch (Throwable e) {
                System.err.println("Error occurred during query execution: " + e);
                e.printStackTrace(System.err);
                result.setError(e);
            } finally {
                if (c!=null) {
                    ConnectionPool.getInstance().freeConnection(server, c);
                }
            }
            result.setExecutionTime(System.currentTimeMillis() - startTime);
            return result;
        }

        @Override
        protected void done() {
            if (pm != null) {
                pm.close();
            }
            try {
                if (isCancelled()) {
                    studioPanel.queryExecutionComplete(-1, null, null);
                } else {
                    QueryResult result = get();
                    studioPanel.queryExecutionComplete(result.getExecutionTime(), result.getResult(), result.getError());
                }
            } catch (Exception e) {
                System.err.println("Ops... It wasn't expected: " + e);
                e.printStackTrace(System.err);
            }
        }
    }
}