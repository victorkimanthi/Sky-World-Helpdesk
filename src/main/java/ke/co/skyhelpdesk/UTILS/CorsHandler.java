package ke.co.skyhelpdesk.UTILS;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class CorsHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Origin"), "*");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Methods"),
                "POST, GET, OPTIONS, PUT, PATCH, DELETE");
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Headers"),
                "Content-Type,Accept,HandlerAuthorizationLayer,AuthToken,access-token,Authorization,RequestReference");
        exchange.setStatusCode(200);
    }
}