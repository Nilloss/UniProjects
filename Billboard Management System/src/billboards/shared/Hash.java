package billboards.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Class containing hashing method
 * @author  Jack Nielsen, Jack Mcsweeney
 */
public class Hash {

    public static String getHash(String value) {
        String hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] allBytes = md.digest(value.getBytes());

            //needs to be converted to hex here? boilerplate
            StringBuilder string = new StringBuilder();
            for (int i = 0; i< allBytes.length; i++){
                string.append(Integer.toString((allBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = string.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }
}
