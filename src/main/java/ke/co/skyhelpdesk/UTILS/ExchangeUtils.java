package ke.co.skyhelpdesk.UTILS;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.URLUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.HashMap;

public class ExchangeUtils {
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";

    public static String getRequestBody(HttpServerExchange exchange) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        try {
            exchange.startBlocking();
            reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString();
    }

    public static String determineContentType(HttpServerExchange exchange) {
        try {
            return determineAorCt(exchange.getRequestHeaders()
                    .get("Content-Type").getFirst());
        } catch (NullPointerException e) {
            return determineAorCt(APPLICATION_JSON);
        }
    }

    public static String determineAccept(HttpServerExchange exchange) {
        try {
            return determineAorCt(exchange.getRequestHeaders().get("Accept").getFirst());
        } catch (NullPointerException e) {
            return determineAorCt(APPLICATION_JSON);
        }
    }

    public static String determineAorCt(String headerValue) {
        switch (headerValue) {
            case APPLICATION_JSON:
                return APPLICATION_JSON;
            case APPLICATION_XML:
                return APPLICATION_XML;
            default:
                return APPLICATION_JSON;
        }
    }

    public static ObjectMapper getRequestObjectMapper(HttpServerExchange exchange) {
        return Converter.getObjectMapper(determineContentType(exchange));
    }

    public static ObjectMapper getResponseObjectMapper(HttpServerExchange exchange) {
        return Converter.getObjectMapper(determineAccept(exchange));
    }

    public static String getFormData(HttpServerExchange exchange, String key) {
        FormData formData = exchange.getAttachment(FormDataParser.FORM_DATA);
        Deque<FormData.FormValue> formValueDeque = formData.get(key);
        String value = null;

        if (formValueDeque != null) {
            value = formValueDeque.getFirst().getValue();
        }
        return value;
    }

    public static HashMap<String, String> getFormData(HttpServerExchange exchange, String... keys) {
        FormData formData = exchange.getAttachment(FormDataParser.FORM_DATA);
        HashMap<String, String> values = new HashMap<>();
        Deque<FormData.FormValue> formValueDeque = null;

        for (String key : keys) {
            formValueDeque = formData.get(key);
            if (key != null) {
                values.put(key, formValueDeque.getFirst().getValue());
            }
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBodyObject(HttpServerExchange exchange, Class<?> clazz, String body) {
        String contentType = determineContentType(exchange);

        ObjectMapper objectMapper = Converter.getObjectMapper(contentType);

        try {
            if (body == null || body.isEmpty())
                throw new Exception("Empty data string provided");

            Object obj = objectMapper.readValue(body, clazz);
            return (T) obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   /* @SuppressWarnings("unchecked")
    public static <T> T getBodyObjectWithGson(HttpServerExchange exchange, Class<?> clazz, String body) {
        String contentType = determineContentType(exchange);

        ObjectMapper objectMapper = ke.co.skyworld.UTILS.Converter.getObjectMapper(contentType);

        try {
            if (body == null || body.isEmpty())
                throw new Exception("Empty data string provided");

            Object obj = objectMapper.readValue(body, clazz);
            String json = ke.co.skyworld.UTILS.Converter.toJson(obj);
            CustomizedObjectTypeAdapter adapter = new CustomizedObjectTypeAdapter();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(SappleArrayList.class, adapter)
                    .registerTypeAdapter(SappleHashMap.class, adapter)
                    .create();

            Object object = gson.fromJson(json, clazz);

            return (T) object;
        } catch (Exception e) {
            ExchangeResponse.sendInternalServerError(exchange, Misc.getTransactionWrapperStackTrace(e), "Unable to marshall the payload provided. Unacceptable format");

            return null;
        }
    }*/

    public static String getPathVar(HttpServerExchange exchange, String pathVarId) {

        PathTemplateMatch pathMatch =
                exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        StringBuilder builder = new StringBuilder();
        URLUtils.decode(pathMatch.getParameters().get(pathVarId), StandardCharsets.UTF_8.name(), true, builder);
        return builder.toString();
    }

    /*public String getPathVar(HttpServerExchange exchange, String pathVarId) {
        PathTemplateMatch pathMatch =
                exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String pv = pathMatch.getParameters().get(pathVarId);
        return pv;
    }
*/
    public static String getQueryParam(HttpServerExchange exchange, String key) {
        Deque<String> param = exchange.getQueryParameters().get(key);
        String paramStr = null;

        if (param != null && !param.getFirst().equals(""))
        {
            paramStr = param.getFirst();
            try {
                paramStr = URLDecoder.decode(paramStr,StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        return paramStr;
    }

    public static HashMap<String, String> getQueryParams(HttpServerExchange exchange, String... keys) {

        HashMap<String, String> params = new HashMap<>();
        Deque<String> param = null;

        for (String key : keys) {
            param = exchange.getQueryParameters().get(key);

            if (param != null && !param.getFirst().equals(""))
            {
                String paramStr = param.getFirst();
                try {
                    paramStr = URLDecoder.decode(paramStr,StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                params.put(key, paramStr);

            }
        }
        
        return params;
    }

    public static int[] getPageAndPageSize(HttpServerExchange exchange) {
        int[] pageAndPageSize = new int[]{1, 10};

        Deque<String> page = exchange.getQueryParameters().get("page");
        Deque<String> pageSize = exchange.getQueryParameters().get("pageSize");

        if (page != null) {
            try {
                pageAndPageSize[0] = Integer.parseInt(page.getFirst());
            } catch (Exception ignore) {

            }
        }

        if (pageSize != null) {
            try {
                pageAndPageSize[1] = Integer.parseInt(pageSize.getFirst());
            } catch (Exception ignore) {

            }
        }
        return pageAndPageSize;
    }


}
