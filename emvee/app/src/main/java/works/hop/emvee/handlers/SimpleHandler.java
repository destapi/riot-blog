package works.hop.emvee.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class SimpleHandler extends AbstractHandler {

    @Override
    public void handle(String target, Request jettyRequest, HttpServletRequest request, HttpServletResponse response) {
        // Mark the request as handled so that it
        // will not be processed by other handlers.
        jettyRequest.setHandled(true);
    }
}
