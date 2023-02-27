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

public class RemoveCustomer implements HttpHandler {
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        LinkedHashMap<String, Object> queryData = new LinkedHashMap<>();
        Map<String, List<String>> parameters;
        List<LinkedHashMap<String, Object>> results;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        QueryHandler queryHandler = new QueryHandler();
        String sqlQuery = "delete  from user_account where user_account_id=:user_account_id";
//        String sqlQueryThree = "delete  from access_tokens  where user_id=:user_id";
//        String sqlQueryTwo = "select  email from user_account WHERE user_account_id=(:user_account_id)";
        String sqlQueryTwo = "select  user_account.email,user_account_application_user_group.application_user_group_id from user_account join user_account_application_user_group on user_account.user_account_id = user_account_application_user_group.user_account_id  WHERE user_account.user_account_id=:user_account_id";

        try {
            if (!checkAccessTokenValidity(httpServerExchange)) {
                System.out.println("Your session has ended.Please login again to access that resource.");
            } else {
                parameters=getRequestParameters(httpServerExchange);
                System.out.println("parameters:"+parameters);
                queryData.put("user_account_id", parameters.get("id").get(0));
                if (queryData != null) {
                    if (queryData.containsValue("")) {
                        response.put("status", "ERROR");
                        response.put("error", "Check to ensure that you have entered all the required fields correctly.");
                        APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                    } else {
                        System.out.println("queryData" + queryData);
                        results = queryHandler.get(sqlQueryTwo, queryData);
                        if (!results.get(0).get("application_user_group_id").equals(2)) {
                            response.put("status", "error");
                            response.put("message", "That user is not a customer.You can not delete a user who is not a customer.");
                            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                        } else {
//                     queryHandler.add(sqlQueryThree,queryData);
                            queryHandler.add(sqlQuery, queryData);
                            response.put("status", "SUCCESS");
                            response.put("message", "The customer  with email as " + results.get(0).get("email") + " has been removed from the system.");
                            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                        }
                    }
                }
                else {
                    response.put("status", "ERROR");
                    response.put("error", "You have not provided any data on the required fields.");
                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            APIResponses.sendResponses(httpServerExchange,response,StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
