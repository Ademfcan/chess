package chessserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;

public class EmbeddedChessEndpoint {

    public static void main(String[] args) throws Exception {

        Server server = new Server();

        // --- TLS / SSL Configuration ---
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer()); // enable HTTPS-specific settings

        // Create SSL Connector on port 8443
        ServerConnector sslConnector = new ServerConnector(
                server,
                new SslConnectionFactory(SslFactoryLoader.createSslContextFactory(), "http/1.1"),
                new HttpConnectionFactory(https)
        );
        sslConnector.setPort(8443);

        // Set the SSL connector as the server connector
        server.addConnector(sslConnector);

        // --- Servlet and WebSocket Context ---
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Initialize WebSocket server container
        JakartaWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            wsContainer.addEndpoint(ChessEndpoint.class);
        });

        server.start();
        System.out.println("Server started on wss://localhost:8443/");
        server.join();
    }
}
