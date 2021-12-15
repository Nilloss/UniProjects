package billboards.server;

import billboards.shared.Hash;

/**
 * This class is responsible for authenticating users
 *
 * @author  Jack Nielsen
 */
public class Authentication {

    //fields
    private static Database db = new Database();
    public static String authenticationStatus;

    /**
     * Main boolean method for checking user and password against the database
     * @param username
     * @param hashedPassword
     * @return
     */

    public static boolean verifyLogin(String username, String hashedPassword){
        //retrieve user db fields
        String[] userData = null;
        try {
            userData = db.get("user","username",username);
        } catch (Exception e) {
            e.printStackTrace();
            authenticationStatus = "Invalid username";
            return false;
        }

        //fields from db
        String userSalt = userData[2];
        String userHash = userData[3];

        String hashAndSalted = hashedPassword + userSalt;
        String finalHashed = Hash.getHash(hashAndSalted);
        //Compare hash in db to hash generated from fields received from cp
        if(userHash.equals(finalHashed)){
            return true;
        }

        //Error identifying
        authenticationStatus = "Invalid password";
        return false;
    }
}
