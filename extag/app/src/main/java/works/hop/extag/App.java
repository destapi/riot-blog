package works.hop.extag;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.IOException;

public class App {

    private static ConnectionFactory configureSsl(HttpConnectionFactory https) {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath("extag/.env/server-keystore");
        sslContextFactory.setKeyStorePassword("changeme");
        return new SslConnectionFactory(sslContextFactory, https.getProtocol());
    }

    private static void configureConnector(Server server) {
        // The plain HTTP configuration.
        HttpConfiguration plainConfig = new HttpConfiguration();
        // The secure HTTP configuration.
        HttpConfiguration secureConfig = new HttpConfiguration(plainConfig);

        // The number of acceptor threads.
        int acceptors = 1;
        // The number of selectors.
        int selectors = 1;
        // Create a ServerConnector instance.
        // First, create the secure connector for HTTPS and HTTP/2.
        HttpConnectionFactory https = new HttpConnectionFactory(secureConfig);
        HTTP2ServerConnectionFactory http2 = new HTTP2ServerConnectionFactory(secureConfig);
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol(https.getProtocol());
        ConnectionFactory ssl = configureSsl(https);
        ServerConnector secureConnector = new ServerConnector(server, acceptors, selectors, ssl, alpn, http2, https);
        secureConnector.setPort(8443);

        // Second, create the plain connector for HTTP.
        HttpConnectionFactory http = new HttpConnectionFactory(plainConfig);
        ServerConnector plainConnector = new ServerConnector(server, acceptors, selectors, http);
        server.addConnector(plainConnector);

        // Configure TCP/IP parameters.
        // The port to listen to.
        plainConnector.setPort(8080);
        // The address to bind to.
        plainConnector.setHost("127.0.0.1");

        // The TCP accept queue size.
        plainConnector.setAcceptQueueSize(128);
        // Add the Connector to the Server
        server.addConnector(plainConnector);
    }

    private static void simpleHandler(ContextHandlerCollection chc) {
        // Create a ContextHandler with contextPath.
        ContextHandler context = new ContextHandler("/shop");
        context.setHandler(new AbstractHandler() {
            @Override
            public void handle(String path, Request request, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
                res.getWriter().println("You reached the club house. Leave a message");
            }
        });
        chc.addHandler(context);
    }

    private static void resourceHandler(HandlerList list) throws IOException {
        // Create and configure a ResourceHandler.
        ResourceHandler handler = new ResourceHandler();
        // Configure the directory where static resources are located.
        handler.setBaseResource(Resource.newResource("extag/www"));
        // Configure directory listing.
        handler.setDirAllowed(false);
        // Configure welcome files.
        handler.setWelcomeFiles(new String[]{"index.html"});
        // Configure whether to accept range requests.
        handler.setAcceptRanges(true);
        list.addHandler(handler);
    }

    public static void main(String[] args) throws Exception {
        // Create and configure a ThreadPool.
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setName("extag-server");

        // Create a Server instance.
        Server server = new Server(threadPool);

        // Create a ServerConnector to accept connections from clients.
        configureConnector(server);

        // Create a ContextHandlerCollection to hold contexts.
        HandlerList handlersList = new HandlerList();
        ContextHandlerCollection contextCollection = new ContextHandlerCollection();
        resourceHandler(handlersList);
        simpleHandler(contextCollection);
        handlersList.addHandler(contextCollection);
        handlersList.addHandler(new DefaultHandler());

        // server.setDefaultHandler(new DefaultHandler(false, true));
        server.setHandler(handlersList);

        // Start the Server to start accepting connections from clients.
        server.start();
    }
}
