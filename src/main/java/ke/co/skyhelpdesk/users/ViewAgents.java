package ke.co.skyhelpdesk.users;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyhelpdesk.QueryHandler;
import ke.co.skyhelpdesk.UTILS.APIResponses;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ke.co.skyhelpdesk.UTILS.AccessToken.checkAccessTokenValidity;
import static ke.co.skyhelpdesk.UTILS.GetParams.getRequestParameters;

public class ViewAgents implements HttpHandler {
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String sqlQuery="select user_account.* from user_account join user_account_application_user_group on user_account.user_account_id = user_account_application_user_group.user_account_id where application_user_group_id = 3 ORDER BY date_created DESC LIMIT 10 OFFSET :OFFSET";
        List<LinkedHashMap<String, Object>> results;
        int pageNumber;
        LinkedHashMap<String, Object> resultsMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> queryDataTwo = new LinkedHashMap<>();
        Map<String, List<String>> parameters = getRequestParameters(httpServerExchange);
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        QueryHandler queryHandler = new QueryHandler();

        try {
            if (!checkAccessTokenValidity(httpServerExchange)) {
                System.out.println("Your session has ended.Please login again to access that resource.");
            } else {
                pageNumber = Integer.parseInt(parameters.get("p").get(0));
                queryDataTwo.put("OFFSET",(pageNumber-1)*10);
                results=queryHandler.get(sqlQuery,queryDataTwo);

                for (LinkedHashMap<String, Object> queryData:results) {
                    queryData.replace("date_created",queryData.get("date_created").toString());
                    queryData.replace("date_modified",queryData.get("date_modified").toString());
                    queryData.replace("first_time_password_expires_at",queryData.get("first_time_password_expires_at").toString());
                }
                resultsMap.put("status", "SUCCESS");
                resultsMap.put("message", "The list of agents has been retrieved successfully");
                resultsMap.put("data", results);
                APIResponses.sendResponses(httpServerExchange, resultsMap, StatusCodes.OK);
            }
        }catch (Exception e){
            e.printStackTrace();
            response.put("status","ERROR");
            response.put("error",e.getMessage());
            APIResponses.sendResponses(httpServerExchange,response,StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
