package ke.co.skyhelpdesk.organization_types;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyhelpdesk.QueryHandler;
import ke.co.skyhelpdesk.UTILS.APIResponses;
import ke.co.skyhelpdesk.UTILS.ExchangeUtils;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class AddOrganizationType implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) {
        LinkedHashMap<String, Object> queryData;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        QueryHandler queryHandler = new QueryHandler();
        String sqlQuery = "insert into organization_type (organization_type_name,organization_type_description) values (:organization_type_name,:organization_type_description)";
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String,Object>>(){}.getType();
        try {
//            if (!checkAccessTokenValidity(httpServerExchange)) {
//                System.out.println("Your session has ended.Please login again to access that resource.");
//            }
//            else {
                queryData = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);
                System.out.println("queryData:" + queryData);
                if (queryData != null && !queryData.isEmpty()) {
                    if (queryData.containsValue("")) {
                        response.put("status", "ERROR");
                        response.put("Error", "A required field is empty.Check to ensure that you have entered all the required fields.");
                        APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                    } else {
                        try {
                            queryHandler.add(sqlQuery, queryData);
                            response.put("Status", "SUCCESS");
                            response.put("Message", queryData.get("organization_type_name") + " has been added as an organization type");
                            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                        } catch (ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    response.put("status", "ERROR");
                    response.put("Error", "You have not provided any data on the required fields.");
                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.put("status","ERROR");
            response.put("Error",e.getMessage());
            APIResponses.sendResponses(httpServerExchange,response,StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
