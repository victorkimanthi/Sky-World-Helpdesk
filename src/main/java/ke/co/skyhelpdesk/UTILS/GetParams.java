package ke.co.skyhelpdesk.UTILS;

import io.undertow.server.HttpServerExchange;

import java.util.*;

public class GetParams {
    public static Map<String, List<String>> getRequestParameters(HttpServerExchange httpServerExchange) {
        Map<String, List<String>> params = new HashMap<>();
        for (Map.Entry<String, Deque<String>> param : httpServerExchange.getQueryParameters().entrySet()) {
            params.put(param.getKey(), new ArrayList<>(param.getValue()));
        }
        return params;
    }
}
