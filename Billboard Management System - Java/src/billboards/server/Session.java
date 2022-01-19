package billboards.server;

import billboards.shared.Utility;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

/**
 * This class contains the session managing logic
 * @author  Jack Nielsen, Ryan Harrold
 */
public class Session {

    /**
     * Generates a unique session token, stores it in database
     * Includes query to that will auto delete the token after 24 hours
     * @return
     */
    public static String createSessionToken(){
        //Get tokens from db
        Database db = new Database();
        String[][] tokens = db.select("SELECT * FROM session;");

        //This is to ensure that the token is unique
        String token = generateRandomString();
        while(containsToken(tokens,token)){
            token = generateRandomString();
        }

        //Add token to database with event query to delete token after 24 hours
        db.insert("session",token);
        String query = "CREATE EVENT delete_" + randomLetterString() +"\n" +
                "    ON SCHEDULE AT NOW() + INTERVAL 24 HOUR\n" +
                "    DO " +
                "DELETE FROM session WHERE token = '"+token+"';";
        db.execute(query);
        return token;
    }

    /**
     * Boolean method to verify the token by checking with db
     * @param token
     * @return
     */
    public static boolean verifySession(String token){
        //Get tokens from db
        Database db = new Database();
        String[][] tokens = db.select("SELECT * FROM session;");
        //check token
        if(containsToken(tokens,token)){
            return true;
        }
        return false;
    }

    /**
     * This is called when user logs out, it will expire the token and delete it from db
     * @param token
     */
    public static void expireSession(String token){
        Database db = new Database();
        db.delete("session","token",token);
    }

    //<editor-fold default-state="collapsed" desc="private methods">
    private static String randomLetterString(){
        String k = "";
        Random r = new Random();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 25; i++) {
            k += alphabet.charAt(r.nextInt(alphabet.length()));
        }
        return k;
    }


    public static String generateRandomString() {
        final Random r = new SecureRandom();
        byte[] rnd = new byte[32];
        r.nextBytes(rnd);
        String encodedToken = Base64.getEncoder().encodeToString(rnd);
        return encodedToken;
    }

    private static boolean containsToken(String[][] arr, String value){
        for(String[] s : arr){
            if(s[0].equals(value)){
                return true;
            }
        }
        return false;
    }
    //</editor-fold>
}
