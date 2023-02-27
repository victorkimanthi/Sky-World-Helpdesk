package ke.co.skyhelpdesk;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.util.Methods;
import ke.co.skyhelpdesk.UTILS.CorsHandler;
import ke.co.skyhelpdesk.UTILS.Dispatcher;
import ke.co.skyhelpdesk.authentication.AgentLogin;
import ke.co.skyhelpdesk.authentication.ChangePassword;
import ke.co.skyhelpdesk.authentication.CustomerLogin;
import ke.co.skyhelpdesk.authentication.ForgotPassword;
import ke.co.skyhelpdesk.organization_types.AddOrganizationType;
import ke.co.skyhelpdesk.organization_types.EditOrganizationType;
import ke.co.skyhelpdesk.organization_types.RemoveOrganizationType;
import ke.co.skyhelpdesk.organization_types.ViewOrganizationTypes;
import ke.co.skyhelpdesk.organizations.AddOrganization;
import ke.co.skyhelpdesk.organizations.EditOrganizationDetails;
import ke.co.skyhelpdesk.organizations.RemoveOrganization;
import ke.co.skyhelpdesk.organizations.ViewOrganizations;
import ke.co.skyhelpdesk.users.*;
//import ke.co.kycapi.authentication.*;


public class Routes {
    static RoutingHandler organizationType() {
        return Handlers.routing()
                .post("organizationType", new BlockingHandler(new AddOrganizationType()))
                .get("organizationType", new Dispatcher(new ViewOrganizationTypes()))
                .put("organizationType", new BlockingHandler(new EditOrganizationType()))
                .delete("organizationType", new BlockingHandler(new RemoveOrganizationType()))
                .add(Methods.OPTIONS, "/*", new CorsHandler());
    }

    static RoutingHandler organizations() {
        return Handlers.routing()
                .post("organization", new BlockingHandler(new AddOrganization()))
                .get("allOrganizations", new Dispatcher(new ViewOrganizations()))
                .put("organization", new BlockingHandler(new EditOrganizationDetails()))
                .delete("organization", new BlockingHandler(new RemoveOrganization()))
                .add(Methods.OPTIONS, "/*", new CorsHandler());
    }

    static RoutingHandler users() {
        return Handlers.routing()
                .post("agent", new BlockingHandler(new AddAgent()))
                .post("customer", new BlockingHandler(new AddCustomer()))
                .get("agents", new Dispatcher(new ViewAgents()))
                .get("customers", new Dispatcher(new ViewCustomers()))
                .put("agent", new BlockingHandler(new EditAgent()))
                .put("customer", new BlockingHandler(new EditCustomer()))
                .delete("agent", new BlockingHandler(new RemoveAgent()))
                .delete("customer", new BlockingHandler(new RemoveCustomer()))
                .add(Methods.OPTIONS, "/*", new CorsHandler());
    }

    static RoutingHandler authentication() {
        return Handlers.routing()
                .post("agentLogin", new BlockingHandler(new AgentLogin()))
                .post("customerLogin", new BlockingHandler(new CustomerLogin()))
                .put("passwordChange", new BlockingHandler(new ChangePassword()))
                .put("forgotPassword", new BlockingHandler(new ForgotPassword()))
                .add(Methods.OPTIONS, "/*", new CorsHandler());
    }

    private static EagerFormParsingHandler uploadHandler(HttpHandler next) {
        return new EagerFormParsingHandler(
                FormParserFactory
                        .builder()
                        .addParser(new MultiPartParserDefinition())
                        .build()
        ).setNext(next);
    }
}
