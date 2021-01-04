package studio.kdb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadQKeywords {

    private static final Logger log = LogManager.getLogger();

    public ReloadQKeywords(final Server server) {
        if (server != null) {
            Runnable runner = () -> {
                kx.c c = null;
                Object r = null;

                try {
                    c = ConnectionPool.getInstance().leaseConnection(server);
                    ConnectionPool.getInstance().checkConnected(c);
                    r = c.k(new K.KCharacterVector("key`.q"));
                }
                catch (Throwable t) {
                    log.error("Error in getting connection to {}", server.getConnectionString(true), t);
                    ConnectionPool.getInstance().purge(server);
                    c = null;
                }
                finally {
                    if (c != null)
                        ConnectionPool.getInstance().freeConnection(server,c);
                }
                if (r instanceof K.KSymbolVector)
                    Config.getInstance().saveQKeywords((String[]) ((K.KSymbolVector) r).getArray());
            };
            Thread t = new Thread(runner);
            t.setName("QKeywordReloader");
            t.setDaemon(true);
            t.start();
        }
    }
}
