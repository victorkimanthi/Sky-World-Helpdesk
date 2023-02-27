package ke.co.skyhelpdesk.UTILS;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;

public class CustomersPdf implements HttpHandler {
//    public static void main(String[] args) throws Exception {
public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

    Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/kyc",
                "victor",
                "victor@2022"
        );

        String sqlQuery = "SELECT * FROM user where status='approved'";
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        String htmlText;
    LinkedHashMap<String, Object> response = new LinkedHashMap<>();

    StringBuilder stringBuilder1 = new StringBuilder(
//                "<Doctype! html>" +
                        "<html>\n" +
                        "  <head>\n" +
                        "<meta charset=\"utf-8\"></meta>" +
                        "    <title>Customers</title>\n" +
                        "<style> table,tr,th,td{border: 1px solid;} table{border-collapse: collapse;}" +
                        "th{height:70px;} td{padding:15px;height:30px;} table{width: 100%;}" +
                        "</style>"+
                        "  </head>\n" +
                        "\n" +
                        "  <body>\n" +
                        "    <h1 style=\"text-align:center;color:#04AA6D;\">List of saccos customers</h1>\n" +
                        "    <table>" +
//                        "<tr><th colspan=\"19\"><h1 style=\"text-align:center;color:#04AA6D;\">List of saccos customers</h1></th></tr>"+
                        "<tr>" +
                        "<th>Email Address</th>" +
                        "<th>First Name</th>" +
                        "<th>Middle Name</th>" +
                        "<th>Last Name</th>" +
                        "<th>Mobile Number</th>" +
                        "<th>Marital Status</th>" +
                        "<th>Gender</th>" +
                        "<th>Name of next of kin</th>" +
                        "<th>Relationship with next of kin</th>" +
                        "<th>Date of birth</th>" +
                        "<th>Postal code</th>" +
                        "<th>Monthly income</th>" +
                        "<th>Status</th>" +
                        "<th>Organization id</th>" +
                        "<th>City/town id</th>" +
                        "<th>Nationality id</th>" +
                        "<th>County id</th>" +
                        "<th>Occupation name</th>" +
                        "<th>Nationality id number</th>" +
                        "</tr>");

        StringBuilder stringBuilder2;
        StringBuilder stringBuilder3;
//        System.out.println("resultSet:"+resultSet);

//        while (resultSet.next()) {
//            stringBuilder2 = new StringBuilder(
//                    "<tr>" +
//                    "<td>" + resultSet.getString("user_email_address") + "</td>" +
//                            "<td>" + resultSet.getString("first_name") + "</td>" +
//                            "<td>" + resultSet.getString("middle_name") + "</td>" +
//                            "<td>" + resultSet.getString("last_name") + "</td>" +
//                            "<td>" + resultSet.getString("user_mobile_no") + "</td>" +
//                            "<td>" + resultSet.getString("marital_status") + "</td>" +
//                            "<td>" + resultSet.getString("gender") + "</td>" +
//                            "<td>" + resultSet.getString("name_of_next_of_kin") + "</td>" +
//                            "<td>" + resultSet.getString("relationship_with_next_of_kin") + "</td>" +
//                            "<td>" + resultSet.getString("date_of_birth") + "</td>" +
//                            "<td>" + resultSet.getString("postal_code") + "</td>" +
//                            "<td>" + resultSet.getDouble("monthly_income") + "</td>" +
//                            "<td>" + resultSet.getString("status") + "</td>" +
//                            "<td>" + resultSet.getInt("organization_id") + "</td>" +
//                            "<td>" + resultSet.getInt("city_town_id") + "</td>" +
//                            "<td>" + resultSet.getInt("nationality_id") + "</td>" +
//                            "<td>" + resultSet.getInt("county_id") + "</td>" +
//                            "<td>" + resultSet.getString("occupation_name") + "</td>" +
//                            "<td>" + resultSet.getString("nationality_id_number") + "</td>" +
//                            "</tr>" +
//                            "</table>" +
//                            "  </body>\n" +
//                            "</html>");
//        }
        while (resultSet.next()) {
            stringBuilder2 = new StringBuilder(
                    "<tr>" +
                            "<td>" + resultSet.getString("user_email_address") + "</td>" +
                            "<td>" + resultSet.getString("first_name") + "</td>" +
                            "<td>" + resultSet.getString("middle_name") + "</td>" +
                            "<td>" + resultSet.getString("last_name") + "</td>" +
                            "<td>" + resultSet.getString("user_mobile_no") + "</td>" +
                            "<td>" + resultSet.getString("marital_status") + "</td>" +
                            "<td>" + resultSet.getString("gender") + "</td>" +
                            "<td>" + resultSet.getString("name_of_next_of_kin") + "</td>" +
                            "<td>" + resultSet.getString("relationship_with_next_of_kin") + "</td>" +
                            "<td>" + resultSet.getString("date_of_birth") + "</td>" +
                            "<td>" + resultSet.getString("postal_code") + "</td>" +
                            "<td>" + resultSet.getDouble("monthly_income") + "</td>" +
                            "<td>" + resultSet.getString("status") + "</td>" +
                            "<td>" + resultSet.getInt("organization_id") + "</td>" +
                            "<td>" + resultSet.getInt("city_town_id") + "</td>" +
                            "<td>" + resultSet.getInt("nationality_id") + "</td>" +
                            "<td>" + resultSet.getInt("county_id") + "</td>" +
                            "<td>" + resultSet.getString("occupation_name") + "</td>" +
                            "<td>" + resultSet.getString("nationality_id_number") + "</td>" +
                            "</tr>");
            stringBuilder1.append(stringBuilder2);
        }

        stringBuilder3=new StringBuilder("</table>" +
                "  </body>\n" +
                "</html>");
//        stringBuilder1.append(stringBuilder2);
        stringBuilder1.append(stringBuilder3);
        htmlText= String.valueOf(stringBuilder1);

        FileWriter fWriter;
        BufferedWriter writer;
        try {
            fWriter = new FileWriter("customers.html");
            writer = new BufferedWriter(fWriter);
            writer.write(htmlText);
            writer.newLine();
            writer.close(); //make sure you close the writer object
            System.out.println("html created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (OutputStream os = new FileOutputStream("customers.pdf")) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            builder.withUri("file:customers.html");
            // set output to an output stream set
            builder.toStream(os);
            // Run the XHTML/XML to PDF conversion and
            builder.run();
            //prints the message if the PDF is created successfully
            System.out.println("PDF created");
            String filePath="/home/victor/IdeaProjects/KycAPI/customers.pdf";
            FilesResponse.sendFile(httpServerExchange,filePath);
        }catch (IOException ioException){
            ioException.printStackTrace();
            response.put("status","ERROR");
            response.put("Error",ioException.getMessage());
            APIResponses.sendResponses(httpServerExchange, response, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
