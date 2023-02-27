package ke.co.skyhelpdesk.UTILS;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyhelpdesk.QueryHandler;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class AccessToken {
    public static boolean checkAccessTokenValidity(HttpServerExchange httpServerExchange) throws SQLException, ClassNotFoundException {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        LinkedHashMap<String, Object> queryData = new LinkedHashMap<>();
        QueryHandler queryHandler = new QueryHandler();
        String sqlQuery = "select user_account_id from access_token where token=:token and expires_at < NOW()";
//        String sqlQueryTwo = "DELETE FROM access_tokens where user_id=:user_id";
        String sqlQueryTwo = "DELETE FROM access_token where token=:token";
        String sqlQueryThree="select token from access_token where token=:token";

        if (httpServerExchange.getRequestHeaders().get("access-token")== null) {
            response.put("Error", "The required header 'access-token' is missing.");
            System.out.println("Here " + httpServerExchange.getRequestHeaders().get("access-token"));
            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
            return false;
        }
        String  accessToken=httpServerExchange.getRequestHeaders().get("access-token").get(0);
        queryData.put("token", accessToken);
//            System.out.println("access_token:"+accessToken);
        if(queryHandler.get(sqlQueryThree,queryData).isEmpty()){
            System.out.println("This token does not exist.Use a valid token");
            response.put("Error", "This token does not exist.Use a valid token.");
            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.FORBIDDEN);
            return false;
        }else {
//            System.out.println("user_id:" + queryHandler.get(sqlQuery, queryData));
            if (!queryHandler.get(sqlQuery, queryData).isEmpty()) {
                response.put("Error", "Your session has expired.Please login again to access that resource.");
                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.FORBIDDEN);
//                System.out.println("user_id:" + queryHandler.get(sqlQuery, queryData).get(0).get("user_id"));
//                queryHandler.add(sqlQueryTwo, queryHandler.get(sqlQuery, queryData).get(0));
                queryHandler.add(sqlQueryTwo,queryData);
                return false;
            } else {
                return true;
            }
        }
            //return true;
        }
    }

