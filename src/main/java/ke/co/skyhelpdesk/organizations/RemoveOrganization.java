package ke.co.skyhelpdesk.organizations;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyhelpdesk.QueryHandler;
import ke.co.skyhelpdesk.UTILS.APIResponses;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ke.co.skyhelpdesk.UTILS.GetParams.getRequestParameters;

public class RemoveOrganization  implements HttpHandler {
    public void handleRequest(HttpServerExchange httpServerExchange) {

        LinkedHashMap<String, Object> queryData = new LinkedHashMap<>();
        Map<String, List<String>> parameters;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        List<LinkedHashMap<String, Object>> results;
        QueryHandler queryHandler = new QueryHandler();
        String sqlQuery = "select  organization_name from organization WHERE organization_id=(:organization_id)";
        String sqlQueryThree = "DELETE FROM  organization WHERE organization_id=(:organization_id)";

        try {
//            if (!checkAccessTokenValidity(httpServerExchange)) {
//                System.out.println("Your session has ended.Please login again to access that resource.");
//            } else {
            parameters=getRequestParameters(httpServerExchange);
            System.out.println("parameters:"+parameters);
            queryData.put("organization_id", parameters.get("id").get(0));
            if (queryData != null) {
                if (queryData.containsValue("")) {
                    response.put("status", "ERROR");
                    response.put("error", "A required field is empty.Check to ensure that you have entered all the required fields.");
                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                } else {

                    try {
                        results=queryHandler.get(sqlQuery,queryData);
                        if(results.isEmpty()){
                            response.put("status", "ERROR");
                            response.put("error", "That organization  does not exist.");
                            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                        }else {
                            queryHandler.add(sqlQueryThree, queryData);
                            response.put("status", "SUCCESS");
                            response.put("message", results.get(0).get("organization_name") + " has been removed  as an organization.");
                            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

            } else {
                response.put("status", "ERROR");
                response.put("error", "You have not provided any data on the required fields.");
                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
            }
//            }
        }catch (Exception e) {
            e.printStackTrace();
            response.put("status","ERROR");
            response.put("error",e.getMessage());
            APIResponses.sendResponses(httpServerExchange,response,StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
