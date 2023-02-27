package ke.co.skyhelpdesk.authentication;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyhelpdesk.UTILS.APIResponses;
import ke.co.skyhelpdesk.UTILS.ExchangeUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ke.co.skyhelpdesk.QueryHandler;

import static ke.co.skyhelpdesk.UTILS.AccessToken.checkAccessTokenValidity;


public class ChangePassword implements HttpHandler {

    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        String changePassQuery = "update user_account set user_password=:user_password,is_ftp=false where email=:email";
//        String changePassQuery2 = "update user_account set user_password=:user_password where email=:email";
        String sqlQuery = "select national_id_number,user_password,email,is_ftp from user_account where user_account_id=:user_account_id";
        String sqlQueryTwo = "select user_account_id from access_token where token=:token";
        String deleteTokenQuery = "delete from access_token where user_account_id=:user_account_id";
        QueryHandler queryHandler = new QueryHandler();
        LinkedHashMap<String, Object> queryData;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        LinkedHashMap<String, Object> tokenMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> idMap = new LinkedHashMap<>();
        List<LinkedHashMap<String, Object>> results;
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();
        String passwordToHash = null;
        boolean isFtp;
        String nationalIdNo;
        String userPassword;
        try {
            if (!checkAccessTokenValidity(httpServerExchange)) {
                System.out.println("Your session has ended.Please login again to access that resource.");
            } else {
                queryData = gson.fromJson(ExchangeUtils.getRequestBody(httpServerExchange), type);

                if (queryData != null && !queryData.isEmpty()) {

                    if (queryData.containsValue("")) {
                        response.put("status", "ERROR");
                        response.put("Error", "A required field is empty.Check to ensure that you have entered all the required fields.");
                        APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                    } else {
                        System.out.println("httpServerExchange.getRequestHeaders().get(access-token):"+httpServerExchange.getRequestHeaders().get("access-token").get(0));
                        tokenMap.put("token",httpServerExchange.getRequestHeaders().get("access-token").get(0));
                        idMap.put("user_account_id",queryHandler.get(sqlQueryTwo,tokenMap).get(0).get("user_account_id"));
                        System.out.println("idMap:"+idMap);
                        results = queryHandler.get(sqlQuery, idMap);
                        System.out.println("results:"+results);
                        isFtp = (boolean) results.get(0).get("is_ftp");
                        nationalIdNo = (String) results.get(0).get("national_id_number");
                        userPassword = (String) results.get(0).get("user_password");

                        if (isFtp) {
                            if (userPassword.equals(queryData.get("old_password"))) {
                                String secureNewPassword = Hashing.sha256().hashString(nationalIdNo + queryData.get("new_password").toString(), StandardCharsets.UTF_8).toString();
                                String confirmedNewPassword = Hashing.sha256().hashString(nationalIdNo + queryData.get("confirmed_new_password").toString(), StandardCharsets.UTF_8).toString();
                                System.out.println("secureNewPassword:" + secureNewPassword);
                                System.out.println("confirmedNewPassword:" + confirmedNewPassword);
                                if (!secureNewPassword.equals(confirmedNewPassword)) {
                                    response.put("status", "ERROR");
                                    response.put("Error", "The new password does not match the confirmed password.Please make sure they match to proceed.");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                                } else {
                                    System.out.println("confirmedNewPassword:" + confirmedNewPassword);
                                    queryData.put("user_password", confirmedNewPassword);
                                    queryData.put("email", results.get(0).get("email"));
                                    queryData.remove("old_password");
                                    queryData.remove("new_password");
                                    queryData.remove("confirmed_new_password");
                                    queryHandler.add(changePassQuery, queryData);
//                                    user_id_map.put("user_account_id",idMap.get("user_account_id"));
                                    queryHandler.add(deleteTokenQuery,idMap);
                                    response.put("status", "SUCCESS");
                                    response.put("message", "Password has been updated successfully");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                                }
                            } else {
                                response.put("status", "ERROR");
                                response.put("Error", "The old password entered is incorrect.Please make sure it's correct first.");
                                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                            }
                        }else {

                            for (Map.Entry<String, Object> entry : queryData.entrySet()) {
                                if (entry.getKey().equals("old_password")) {
                                    Object value = entry.getValue();
                                    passwordToHash = (String) value;
                                }
                            }
                            System.out.println("results:" + results);
                            String securePassword = Hashing.sha256().hashString(nationalIdNo + passwordToHash, StandardCharsets.UTF_8).toString();
                            System.out.println("securePassword:" + securePassword);
                            System.out.println("queryData:" + queryData);

                            if (userPassword.equals(securePassword)) {
                                String secureNewPassword = Hashing.sha256().hashString(nationalIdNo + queryData.get("new_password").toString(), StandardCharsets.UTF_8).toString();
                                String confirmedNewPassword = Hashing.sha256().hashString(nationalIdNo + queryData.get("confirmed_new_password").toString(), StandardCharsets.UTF_8).toString();
                                System.out.println("secureNewPassword:" + secureNewPassword);
                                System.out.println("confirmedNewPassword:" + confirmedNewPassword);
                                if (!secureNewPassword.equals(confirmedNewPassword)) {
                                    response.put("status", "ERROR");
                                    response.put("Error", "The new password does not match the confirmed password.Please make sure they match to proceed.");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                                } else {
                                    System.out.println("confirmedNewPassword:" + confirmedNewPassword);
                                    queryData.put("user_password", confirmedNewPassword);
                                    queryData.put("email", results.get(0).get("email"));
                                    queryData.remove("old_password");
                                    queryData.remove("new_password");
                                    queryData.remove("confirmed_new_password");
                                    queryHandler.add(changePassQuery, queryData);
                                    queryHandler.add(deleteTokenQuery,idMap);
                                    response.put("status", "SUCCESS");
                                    response.put("message", "Password has been updated successfully");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                                }
                            } else {
                                response.put("status", "ERROR");
                                response.put("error", "The old password entered is incorrect.Please make sure it's correct first.");
                                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                            }
                        }
                    }
                }else {
                    response.put("status", "ERROR");
                    response.put("error", "You have not provided any data on the required fields.");
                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
