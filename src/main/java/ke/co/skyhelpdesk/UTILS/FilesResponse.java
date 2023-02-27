package ke.co.skyhelpdesk.UTILS;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;

public class FilesResponse {
    @SuppressWarnings("Duplicates")
    public static void sendFile(HttpServerExchange exchange, String rawPath) {
        File file = new File(rawPath);
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        OutputStream outStream = null;
        FileInputStream inputStream = null;
        int BUFFER_SIZE = 1024 * 100;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        if (file.exists()) {
            //String fileSize = String.valueOf((double) file.length()/1024);
            System.out.println("file responses");
            String exposeHeaders = Headers.CONTENT_DISPOSITION.toString() + ", " + Headers.CONTENT_LENGTH.toString();
            String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/octet-stream");
            exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, file.length());
            exchange.getResponseHeaders().put(Headers.CONTENT_DISPOSITION, headerValue);
            exchange.getResponseHeaders().put(new HttpString("Access-Control-Expose-Headers"), exposeHeaders);
            exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"),"GET");

            try {
                outStream = exchange.getOutputStream();
                inputStream = new FileInputStream(file);

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                System.out.println("is it reaching here............");
                inputStream.close();
                //outStream.flush();
                outStream.close();
            } catch (IOException ioExObj) {
//                sendInternalServerError(exchange, Misc.getTransactionWrapperStackTrace(ioExObj),"Exception While Performing File Operation");
                System.out.println("IO error");
                response.put("status","ERROR");
                response.put("Error",ioExObj.getMessage());
                APIResponses.sendResponses(exchange, response, StatusCodes.INTERNAL_SERVER_ERROR);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outStream != null) {
                        outStream.flush();
                        outStream.close();
                    }
                } catch (IOException ignore) {
                }
            }

        } else {
//            sendNotFound(exchange,"File not found in server.");
            System.out.println("there is no file");
            response.put("status","ERROR");
            response.put("Error","File not found in server.");
            APIResponses.sendResponses(exchange, response, StatusCodes.NOT_FOUND);
        }
    }
}
