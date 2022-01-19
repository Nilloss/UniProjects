package billboards.server;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

/**
 * Class containing the salt method
 * @author  Jack Nielsen, Jack Mcsweeney
 */
public class Salt{

    /**
     * Generates salt upon user creation
     * Encodes into a Base64 string
     * @return
     */
    public static String generateSalt() {
        final Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        return encodedSalt;
    }
}
