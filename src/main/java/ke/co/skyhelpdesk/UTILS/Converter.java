package ke.co.skyhelpdesk.UTILS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * flexicore (ke.co.flexicore.utilities.xml)
 * Created by: elon
 * On: 28 Jul, 2019 28/07/19 20:29
 **/
public class Converter {

    public static String toJson(Object obj) {
        return serialize(obj, ExchangeUtils.APPLICATION_JSON);
    }

    public static String toXml(Object obj) {
        return serialize(obj, ExchangeUtils.APPLICATION_XML);
    }

    public static String serialize(Object obj, String contentType) {
        ObjectMapper objectMapper = getObjectMapper(contentType);
        try {
            String objStr = objectMapper.writeValueAsString(obj);
            return objStr;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Object[] fromJson(String data, Class<?> clazz) {

        ObjectMapper objectMapper = getObjectMapper(ExchangeUtils.APPLICATION_JSON);

        try {
            Object obj = objectMapper.readValue(data, clazz);
            return new Object[]{1, obj};
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{-1, "MARSHALL ERROR", e.getMessage()};
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getObject(String data, Class clazz, String contentType) {
        ObjectMapper objectMapper = getObjectMapper(contentType);
        try {
            Object obj = objectMapper.readValue(data, clazz);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object toHashMap(String objStr, TypeReference T) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = new HashMap<>();
        try {
            map = objectMapper.readValue(objStr, T);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static ObjectMapper getObjectMapper(String contentType) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (contentType.equals(ExchangeUtils.APPLICATION_XML)) {
            JacksonXmlModule xmlModule = new JacksonXmlModule();
            xmlModule.setDefaultUseWrapper(false);
            XmlMapper mapper = new XmlMapper(xmlModule);
            mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
            mapper.setDateFormat(df);  // this works for outbounds but has no effect on inbounds
            mapper.getDeserializationConfig().with(df); // Gave this a shot but still does not sniff strings for a format that we declare
            // should be treated as java.util.Date
            return mapper;
        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
            mapper.setDateFormat(df);  // this works for outbounds but has no effect on inbounds
            mapper.getDeserializationConfig().with(df); // Gave this a shot but still does not sniff strings for a format that we declare
            // should be treated as java.util.Date
            return mapper;
        }
    }
}
