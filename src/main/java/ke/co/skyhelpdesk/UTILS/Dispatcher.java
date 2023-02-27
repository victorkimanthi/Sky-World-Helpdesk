package ke.co.skyhelpdesk.UTILS;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class Dispatcher implements HttpHandler {
    private HttpHandler handler;

    public Dispatcher(HttpHandler handler) {
        this.handler = handler;
    }

    public Dispatcher() {
        this((HttpHandler)null);
    }

    @Deprecated
    public void handleRequest(HttpServerExchange exchange) throws Exception {
//        exchange.startBlocking();
        if (exchange.isInIoThread()) {
            exchange.dispatch(this.handler);
        }
    }

    public HttpHandler getHandler() {
        return this.handler;
    }

    public Dispatcher setRootHandler(HttpHandler rootHandler) {
        this.handler = rootHandler;
        return this;
    }

    private static class Wrapper implements HandlerWrapper {
        private Wrapper() {
        }

        public HttpHandler wrap(HttpHandler handler) {
            return new Dispatcher(handler);
        }
    }

    public static class Builder implements HandlerBuilder {
        public Builder() {
        }

        public String name() {
            return "dispatcher";
        }

        public Map<String, Class<?>> parameters() {
            return Collections.emptyMap();
        }

        public Set<String> requiredParameters() {
            return Collections.emptySet();
        }

        public String defaultParameter() {
            return null;
        }

        public HandlerWrapper build(Map<String, Object> config) {
            return new Wrapper();
        }
    }
}