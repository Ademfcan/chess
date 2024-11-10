package chessserver;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

public class ShutdownListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // No specific actions needed for initialization
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (ChessEndpoint.getDataSource() != null) {
            ChessEndpoint.getDataSource().close();
        }
    }
}