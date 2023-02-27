package ke.co.skyhelpdesk.organizations;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyhelpdesk.QueryHandler;
import ke.co.skyhelpdesk.UTILS.APIResponses;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

public class ViewOrganizations implements HttpHandler {
    public void handleRequest(HttpServerExchange httpServerExchange) {

        List<LinkedHashMap<String,Object>> results;
        String sqlQuery = "select * from organization";
        LinkedHashMap<String,Object> response=new LinkedHashMap<>();
        LinkedHashMap<String,Object> resultsMap=new LinkedHashMap<>();
        QueryHandler queryHandler = new QueryHandler();

        try {
            results = queryHandler.get(sqlQuery);
            for (LinkedHashMap<String, Object> queryData:results) {
                queryData.replace("date_created",queryData.get("date_created").toString());
                queryData.replace("date_modified",queryData.get("date_modified").toString());
            }
            resultsMap.put("Message", "The list of organizations has been retrieved successfully");
            resultsMap.put("status", "SUCCESS");
            resultsMap.put("data", results);
            APIResponses.sendResponses(httpServerExchange, resultsMap, StatusCodes.OK);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.put("status","ERROR");
            response.put("Error",e.getMessage());
            APIResponses.sendResponses(httpServerExchange,response,StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
