package ke.co.skyhelpdesk.users;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyhelpdesk.QueryHandler;
import ke.co.skyhelpdesk.UTILS.APIResponses;
import ke.co.skyhelpdesk.UTILS.ExchangeUtils;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ke.co.skyhelpdesk.UTILS.AccessToken.checkAccessTokenValidity;
import static ke.co.skyhelpdesk.UTILS.GetParams.getRequestParameters;

public class EditAgent implements HttpHandler {
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        LinkedHashMap<String, Object> queryData;
        Map<String, List<String>> parameters;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        QueryHandler queryHandler = new QueryHandler();
        String sqlQuery = "UPDATE  user_account SET first_name=(:first_name),last_name=(:last_name),email=(:email),national_id_number=(:national_id_number),phone_number=(:phone_number),designation = (:designation) where user_account_id=:user_account_id";
        parameters=getRequestParameters(httpServerExchange);
        System.out.println("parameters:"+parameters);
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();

        try {
            queryData = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);
            if (!checkAccessTokenValidity(httpServerExchange)) {
                System.out.println("Your session has ended.Please login again to access that resource.");
            } else {
                queryData.put("user_account_id", parameters.get("id").get(0));
                System.out.println("queryData:"+queryData);
                queryHandler.add(sqlQuery,queryData);
                response.put("status", "SUCCESS");
                response.put("message","The agent's details have been updated successfully");
                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
            }
        } catch (Exception e){
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.INTERNAL_SERVER_ERROR);
        }

    }

}
