package ke.co.skyhelpdesk.UTILS;

/*public class RandomString {
  *//*  public static String generatingRandomAlphanumericString() {
        String generatedString = RandomStringUtils.randomAlphanumeric(50);
        System.out.println(generatedString);
        return generatedString;
    }*//*
}  */
import java.util.UUID;
public class RandomStringGen {
    public static String generatingRandomAlphanumericString()
        {
            //generates random UUID
            UUID uuid=UUID.randomUUID();
            String generatedString= String.valueOf(uuid);
            System.out.println(generatedString);
            return generatedString;
        }
    }

