package ke.co.skyhelpdesk.UTILS;

import com.google.gson.Gson;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class APIResponses {
        public static void sendResponses(HttpServerExchange httpServerExchange,Object responses,int statusCodes) {
            Gson gson=new Gson();
            httpServerExchange.getResponseHeaders()

                    .put(new HttpString("Content-type"), "application/json")
                    .put(new HttpString("Access-Control-Allow-Origin"),"*")
                    .put(new HttpString("Access-Control-Allow-Credentials"),"true")
//                    .put(new HttpString("Access-Control-Allow-Headers ") ,"access-token")
                    .put(new HttpString("Access-Control-Allow-Headers ") ,"*")
                    .put(new HttpString("Access-Control-Allow-Methods")," POST, GET, OPTIONS, PUT, PATCH, DELETE");

            httpServerExchange.setStatusCode(statusCodes);
            httpServerExchange.getResponseSender().send(gson.toJson(responses));
        }
}
