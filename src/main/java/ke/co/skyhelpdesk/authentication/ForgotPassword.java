package ke.co.skyhelpdesk.authentication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import ke.co.skyhelpdesk.QueryHandler;
import ke.co.skyhelpdesk.UTILS.APIResponses;
import ke.co.skyhelpdesk.UTILS.ExchangeUtils;
import ke.co.skyhelpdesk.UTILS.RandomStringGen;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

public class ForgotPassword implements HttpHandler {
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
//      String sqlQuery="update user set user_password=:user_password,otp_expires_at=:otp_expires_at where user_email_address=:user_email_address";
        String sqlQuery="update user_account set user_password=:user_password,first_time_password_expires_at=:first_time_password_expires_at,is_ftp=true where email=:email";
        String generatedPasswordQuery="select * from helpdesk_settings where settings_key='generated_password'";
        String sqlQueryTwo="select is_ftp,first_name from user_account where email=:email";
        QueryHandler queryHandler = new QueryHandler();
        LinkedHashMap<String, Object> queryData = new LinkedHashMap<>();
        List<LinkedHashMap<String, Object>> results;
        List<LinkedHashMap<String, Object>> userAccountDetails = null;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();
        String requestBody = ExchangeUtils.getRequestBody(httpServerExchange);
        String securePassword = null;
        String timePeriod = null;
        long sessionTime = 0;
        LocalDateTime newDateTime = null;
        DateTimeFormatter dateNow = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy HH:mm:ss a");

        try {
            queryData = gson.fromJson(requestBody, type);
            if (queryData != null && !queryData.isEmpty()) {
                if (queryData.containsValue("")) {
                    response.put("status", "ERROR");
                    response.put("Error", "A required field is empty.Check to ensure that you have entered all the required fields.");
                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                }else {
                    securePassword= RandomStringGen.generatingRandomAlphanumericString();
                    results=queryHandler.get(generatedPasswordQuery);

                    //get data from the xml document***************************************************
                    //Create a Document from a file or stream
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    System.out.println("queryHandler.get(generatedPasswordQuery):"+results);
                    ByteArrayInputStream input =  new ByteArrayInputStream(results.get(0).get("settings_value").toString().getBytes(StandardCharsets.UTF_8));
                    Document doc = builder.parse(input);

                    //Build XPath
                    XPath xPath =  XPathFactory.newInstance().newXPath();

                    //Prepare Path expression and evaluate it
                    String expression = "/generated_password_configuration";
                    NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

                    Node nNode = nodeList.item(0);
                    System.out.println("\nCurrent Element :" + nNode.getNodeName());

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        System.out.println("expiry_time:" + eElement.getElementsByTagName("expiry_time").item(0).getTextContent());
                        System.out.println("expiry_time_period:" + eElement.getElementsByTagName("expiry_time_period").item(0).getTextContent());
                        timePeriod= eElement.getElementsByTagName("expiry_time_period").item(0).getTextContent();
                        sessionTime= Long.parseLong((eElement.getElementsByTagName("expiry_time").item(0).getTextContent()));
                    }
                    //**********************************************************************************
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println("dateNow:"+dateNow.format(now));
                    System.out.println("sessionTime:"+sessionTime);

                    userAccountDetails=queryHandler.get(sqlQueryTwo,queryData);

                    if (timePeriod != null) {
                        switch (timePeriod) {
                            case "days" :
                                newDateTime = now.plusDays(sessionTime);
                                queryData.put("first_time_password_expires_at",newDateTime);
                                queryData.put("user_password",securePassword);
                                if(userAccountDetails.isEmpty()){
                                    response.put("status", "ERROR");
                                    response.put("error", "That email does not exist.");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                                }else {
                                    queryHandler.add(sqlQuery, queryData);
                                    response.put("status", "SUCCESS");
                                    response.put("message", "Your  password has been  successfully reset.Check you email to see the new password.");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                                }
                                break;
                            case "hours":
                                newDateTime = now.plusHours(sessionTime);
                                queryData.put("first_time_password_expires_at",newDateTime);
                                queryData.put("user_password",securePassword);

                                if(userAccountDetails.isEmpty()){
                                    response.put("status", "ERROR");
                                    response.put("error", "That email does not exist.");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                                }else {
                                    queryHandler.add(sqlQuery, queryData);
                                    response.put("status", "SUCCESS");
                                    response.put("message", "Your  password has been  successfully reset.Check you email to see the new password.");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                                }
                                break;
                            case "minutes":
                                newDateTime = now.plusMinutes(sessionTime);
                                queryData.put("first_time_password_expires_at",newDateTime);
                                queryData.put("user_password",securePassword);

                                if(userAccountDetails.isEmpty()){
                                    response.put("status", "ERROR");
                                    response.put("error", "That email does not exist.");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
                                }else {
                                    queryHandler.add(sqlQuery, queryData);
                                    response.put("status", "SUCCESS");
                                    response.put("message", "Your  password has been  successfully reset.Check you email to see the new password.");
                                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);
                                }
                                break;
                        }
                    }
                    /*response.put("status", "SUCCESS");
                    response.put("message", "Your  password has been  successfully reset.Check you email to see the new password.");
                    APIResponses.sendResponses(httpServerExchange, response, StatusCodes.OK);*/
                }
            }else {
                response.put("status", "ERROR");
                response.put("error", "You have not provided any data on the required fields.");
                APIResponses.sendResponses(httpServerExchange, response, StatusCodes.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("error",e.getMessage());
            APIResponses.sendResponses(httpServerExchange,response,StatusCodes.INTERNAL_SERVER_ERROR);
        }
        try {
            // email ID of Recipient.
//            String recipient = "victorkimanthi556@gmail.com";
            assert queryData != null;
            String recipient = (String) queryData.get("email");

            // email ID of  Sender.
            String sender = "vickiekimathi2070@gmail.com";

            // using host as localhost
            String host = "smtp.gmail.com";
            String port = "587";

            // Get system properties
            Properties properties = new Properties();

            // Setup mail server
            properties.setProperty("mail.smtp.host", host);
            properties.setProperty("mail.smtp.port", port);
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.ssl.trust", "*");
            properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");


            // Get the default Session object.
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("vickiekimathi2070@gmail.com", "nphxkgkjhnekgudh");
                }
            });

            try {
                // MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Set From Field: adding senders email to from field.
                message.setFrom(new InternetAddress(sender));

                // Set To Field: adding recipient's email to from field.
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                // Set Subject: subject of the email
                message.setSubject("Sky World Helpdesk reset login password");

                // set body of the email.
                //  message.setText("Welcome to Sky World KYC System.Your first time password is : " + (String) queryDataTwo.get("user_password"));
                // Send email.
                assert userAccountDetails != null;
//                if(userAccountDetails.get(0).get("is_ftp").equals(true)){
                String someHtmlMessage = null;

                if (newDateTime != null) {
                    someHtmlMessage = "Hello " + userAccountDetails.get(0).get("first_name")+"! Welcome to Sky World Helpdesk.Your new password is :<b style='color:blue;'>" + securePassword + "</b>.It is going to expire on <b style='color:blue;'>" +dateNow.format(newDateTime) +  "</b>.Please change it before it expires or you will not be able login to the system.";
                }
                message.setContent(someHtmlMessage, "text/html; charset=utf-8");
//                }

                Transport.send(message);
                System.out.println("Mail successfully sent");
            } catch (MessagingException mex) {
                mex.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.put("status","ERROR");
            response.put("Error", e.getMessage());
            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
