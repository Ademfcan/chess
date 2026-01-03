package simulatedplayer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;

public class EmbeddedServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        // Setup context for servlets and websockets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);


        // Initialize Jakarta WebSocket server container
        JakartaWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            // Register your endpoint class
            wsContainer.addEndpoint(EchoWebSocket.class);
        });

        server.start();
        System.out.println("Server started on ws://localhost:8080/Test");
        server.join();
    }
}
