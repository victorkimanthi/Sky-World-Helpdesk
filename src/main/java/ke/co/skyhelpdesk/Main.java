package ke.co.skyhelpdesk;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import ke.co.skyhelpdesk.UTILS.ConfigXMLReader;

import java.util.LinkedHashMap;

public class Main {
    public static void main(String[] args) {

        LinkedHashMap<String,Object> configXMLData;
        configXMLData= ConfigXMLReader.xmlReader();
        String host=configXMLData.get("host").toString();
        int port= Integer.parseInt((String) configXMLData.get("port"));
        QueryHandler.xmlReader();
        PathHandler handler = Handlers.path()
                .addPrefixPath("/helpdesk/organizationTypes", Routes.organizationType())
                .addPrefixPath("/helpdesk/organizations", Routes.organizations())
                .addPrefixPath("/helpdesk/users", Routes.users())
                .addPrefixPath("/helpdesk/userAuthentication", Routes.authentication());
//                .addPrefixPath("/kyc/api", Routes.customerApplication());

        Undertow server = Undertow.builder()
//                .addHttpListener(8080, "127.0.0.1")
                .addHttpListener(port, host)
                .setHandler(handler)
                .build();

        server.start();
    }
}