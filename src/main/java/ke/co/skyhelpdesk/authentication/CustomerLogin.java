package ke.co.skyhelpdesk.authentication;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyhelpdesk.QueryHandler;
import ke.co.skyhelpdesk.UTILS.APIResponses;
import ke.co.skyhelpdesk.UTILS.ExchangeUtils;
import ke.co.skyhelpdesk.UTILS.RandomStringGen;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerLogin implements HttpHandler {
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        LinkedHashMap<String, Object> queryData;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        LinkedHashMap<String, Object> tokenDetails = new LinkedHashMap<>();
        LinkedHashMap<String, Object> queryDataTwo = new LinkedHashMap<>();
        LinkedHashMap<String, Object> organizationNameMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> userTypeMap = new LinkedHashMap<>();
//        LinkedHashMap<String, Object> deleteTokenMap = new LinkedHashMap<>();
        List<LinkedHashMap<String, Object>> results;
//        List<LinkedHashMap<String, Object>> resultsTwo = null;
        QueryHandler queryHandler = new QueryHandler();
        String sqlQueryTwo = "select * from user_account where email=:email";
        String sqlQueryFour = "insert into access_token(token,user_account_id,expires_at) values(:token,:user_account_id,DATE_ADD(NOW(), INTERVAL 1 HOUR ))";
        String userType = "select application_user_group_id from user_account_application_user_group where user_account_id=:user_account_id";
        String organizationNameQuery = "select organization_name from organization where organization_id=:organization_id";
//        String deleteTokenQuery = "delete from access_token where user_account_id=:user_account_id";
        //String query= "update session set otp_validated = 'YES', updated_at = NOW(), expires_at = date_add(NOW(), INTERVAL :session_time MINUTE) where token = :token";

        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();
        queryData = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);
        System.out.println("queryData:" + queryData);
        String passwordToHash = null;
        String accessToken;
        String userPasswordString;
        String nationalIdNo;
        int userAccountId;
        int organizationId;
        boolean isFtp;

        try {
            if (queryData != null && !queryData.isEmpty()) {

                queryDataTwo.put("email", queryData.get("email"));
                results = queryHandler.get(sqlQueryTwo, queryDataTwo);
                if (results.isEmpty()){
                    response.put("status", "ERROR");
                    response.put("error", "That email address does not exist.");
                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                }
                System.out.println("results:" + results);
                userPasswordString = (String) results.get(0).get("user_password");
                nationalIdNo = (String) results.get(0).get("national_id_number");//linked-hashmap containing a nationality_id_number

                LocalDateTime theDateNow = LocalDateTime.now();
                LocalDateTime firstTimePasswordExpiryDate = (LocalDateTime) results.get(0).get("first_time_password_expires_at");
                if (!theDateNow.isBefore(firstTimePasswordExpiryDate) && results.get(0).get("is_ftp").equals(true)) {
                    response.put("status", "ERROR");
                    response.put("error", "Your one time password has already expired.Click on forgot password to generate another password");
                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.FORBIDDEN);
                } else {
                    if (queryData.containsValue("")) {
                        response.put("status", "ERROR");
                        response.put("error", "A required field is empty.Check to ensure that you have entered all the required fields.");
                        APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                    } else {
                        isFtp = (boolean) results.get(0).get("is_ftp");
                        userAccountId = (int) results.get(0).get("user_account_id");
                        organizationId = (int) results.get(0).get("organization_id");

                        if (isFtp) {
                            if (userPasswordString.equals(queryData.get("user_password"))) {

                                accessToken = RandomStringGen.generatingRandomAlphanumericString();
                                tokenDetails.put("token", accessToken);
                                tokenDetails.put("user_account_id", userAccountId);
//
//                                deleteTokenMap.put("user_account_id", userAccountId);
//                                queryHandler.add(deleteTokenQuery, deleteTokenMap);

                                queryHandler.add(sqlQueryFour, tokenDetails);
                                response.put("status", "SUCCESS");
                                response.put("Message", "Login was successful");
                                response.put("access_token", accessToken);
                                userTypeMap.put("user_account_id", userAccountId);
                                organizationNameMap.put("organization_id", results.get(0).get("organization_id"));
                                response.put("user_type_id", queryHandler.get(userType, userTypeMap).get(0).get("application_user_group_id"));
                                response.put("email", queryData.get("email"));
                                response.put("is_ftp", true);
                                response.put("organization_id", organizationId);
                                response.put("organization_name", queryHandler.get(organizationNameQuery, organizationNameMap).get(0).get("organization_name"));
                                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                            } else {
                                response.put("status", "ERROR");
                                response.put("error", "A wrong email or password was entered,please confirm again");
                                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.UNAUTHORIZED);
                            }
                        } else {
                            for (Map.Entry<String, Object> entry : queryData.entrySet()) {
                                if (entry.getKey().equals("user_password")) {
                                    Object value = entry.getValue();
                                    passwordToHash = (String) value;
                                }
                            }
                            System.out.println("passwordToHash:" + passwordToHash);
                            System.out.println("id_number:" + results.get(0).get("national_id_number"));
                            String securePassword = Hashing.sha256().hashString(results.get(0).get("national_id_number") + passwordToHash, StandardCharsets.UTF_8).toString();
                            queryData.replace("user_password", securePassword);
                            System.out.println("queryData:" + queryData);
                            queryData.remove("user_password");

                            if (userPasswordString.equals(securePassword)) {
                                accessToken = RandomStringGen.generatingRandomAlphanumericString();
                                tokenDetails.put("token", accessToken);
                                tokenDetails.put("user_account_id", userAccountId);
                                System.out.println("id:" + userAccountId);
//                                deleteTokenMap.put("user_account_id", userAccountId);
//                                queryHandler.add(deleteTokenQuery, deleteTokenMap);
                                System.out.println("tokenDetails:" + tokenDetails);
                                queryHandler.add(sqlQueryFour, tokenDetails);
                                response.put("status", "SUCCESS");
                                response.put("Message", "Login was successful");
                                response.put("access_token", accessToken);
                                userTypeMap.put("user_account_id", userAccountId);
                                organizationNameMap.put("organization_id", organizationId);
                                response.put("user_type_id", queryHandler.get(userType, userTypeMap).get(0).get("application_user_group_id"));
                                response.put("email", queryData.get("email"));
                                response.put("is_ftp", false);
                                response.put("organization_id", organizationId);
                                response.put("organization_name", queryHandler.get(organizationNameQuery, organizationNameMap).get(0).get("organization_name"));
                                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                            } else {
                                response.put("status", "ERROR");
                                response.put("error", "A wrong email or password was entered,please confirm again");
                                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.UNAUTHORIZED);
                            }
                        }
                    }
                }
            }else {
                response.put("status", "ERROR");
                response.put("error", "You have not provided any data on the required fields.");
                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
