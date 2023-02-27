package ke.co.skyhelpdesk.UTILS;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Base64;

/**
 * Wraps methods to convert base64 string to image and vice-versa.
 *
 * @author Nipendra
 * @author arshavin69ru@gmail.com
 * @version 1.0
 */
public class UploadOneFile {

    /**
     * convert/encode a binary image file to base64 string
     * and save on the disk.
     *
     * @param imgPath  path of image to be encoded on the disk.
     * @param savePath path on disk to save encoded text in a file.
     * @return A string representing the encoded image.
     * @throws Exception
     */
    public static String encodeImage(String imgPath, String savePath) throws Exception {

        // read image from file
        FileInputStream stream = new FileInputStream(imgPath);

        // get byte array from image stream
        int bufLength = 2048;
        byte[] buffer = new byte[2048];

        byte[] data;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int readLength;
        while ((readLength = stream.read(buffer, 0, bufLength)) != -1) {
            out.write(buffer, 0, readLength);
        }

        data = out.toByteArray();

        String imageString = Base64.getEncoder().encodeToString(data);

        FileWriter fileWriter = new FileWriter(savePath);

        fileWriter.write(imageString);

        // close streams
        fileWriter.close();
        out.close();
        stream.close();

        return imageString;

    }

    /**
     * decode/convert base64 string back into an image file and save on disk
     *
     * @param txtPath  path of file on disk which contains base64 string
     * @param savePath path on disk where we want to save the new image created from base64 string.
     * @throws Exception
     */
    public static void decodeImage(String txtPath, String savePath) throws Exception {

        // read from text file
        FileInputStream inputStream = new FileInputStream(txtPath);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 2048;
        byte[] buffer = new byte[len];
        byte[] textData;
        int readLength;

        while ((readLength = inputStream.read(buffer, 0, len)) != -1) {
            out.write(buffer, 0, readLength);
        }

        textData = out.toByteArray();


        byte[] data = Base64.getDecoder().decode(new String(textData));

        // Base64.getDecoder().decode(inputStream.readAllBytes());

        FileOutputStream fileOutputStream = new FileOutputStream(savePath);

        // write array of bytes to an image file
        fileOutputStream.write(data);
        out.close();
        // close streams
        fileOutputStream.close();
        inputStream.close();
    }
}
