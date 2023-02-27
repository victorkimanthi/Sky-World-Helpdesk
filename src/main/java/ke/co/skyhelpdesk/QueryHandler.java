package ke.co.skyhelpdesk;

import ke.co.skyhelpdesk.UTILS.ConfigXMLReader;
import ke.co.skyhelpdesk.UTILS.NamedPreparedStatement;  

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class  QueryHandler {
    public static LinkedHashMap <String,Object> configXMLData;
    public static String url;
    public static String username;
    public static String password;
    public static String className;
    public static void xmlReader(){
        configXMLData=ConfigXMLReader.xmlReader();
        url= (String) configXMLData.get("url");
        username= (String) configXMLData.get("username");
        password= (String) configXMLData.get("password");
        className= (String) configXMLData.get("className");
    }

    //GET FROM DATABASE
    public List<LinkedHashMap<String, Object>> get(String sqlQuery) throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, username, password);
//        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kyc", "victor", "victor@2022");
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, sqlQuery);

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        int count = metaData.getColumnCount();
        List<LinkedHashMap<String, Object>> results = new ArrayList<>();

        while (resultSet.next()) {
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            for (int i = 1; i <= count; i++) {
                result.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            results.add(result);
        }
        resultSet.close();
        connection.close();

        return results;

    }
    public List<LinkedHashMap<String, Object>> get(String sqlQuery, LinkedHashMap<String, Object> values) throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
        Class.forName(className);
//        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kyc", "victor", "victor@2022");
        Connection connection = DriverManager.getConnection(url, username, password);
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, sqlQuery);

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        int count = metaData.getColumnCount();
        List<LinkedHashMap<String, Object>> results = new ArrayList<>();

        while (resultSet.next()) {
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            for (int i = 1; i <= count; i++) {
                result.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            results.add(result);
        }
        resultSet.close();
        connection.close();

        return results;
    }

    public List<LinkedHashMap<String, Object>> getId(String sqlQuery, LinkedHashMap<String, Object> values) throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
         Class.forName(className);
//        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kyc", "victor", "victor@2022");
        Connection connection = DriverManager.getConnection(url, username, password);
        NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, sqlQuery);

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            statement.setObject(key, value);
        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        int count = metaData.getColumnCount();
        List<LinkedHashMap<String, Object>> results = new ArrayList<>();

        while (resultSet.next()) {
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            for (int i = 1; i <= count; i++) {
                result.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            results.add(result);
        }
        resultSet.close();
        connection.close();

        return results;
    }
    //insert into database
        public void add(String sqlQuery, LinkedHashMap < String, Object > values) throws ClassNotFoundException, SQLException {
//            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName(className);
            Connection connection = DriverManager.getConnection(url, username, password);
            NamedPreparedStatement statement = NamedPreparedStatement.prepareStatement(connection, sqlQuery);

            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                //System.out.println("the value: "+value);
                statement.setObject(key, value);
            }
             statement.executeUpdate();
            connection.close();
        }
}


