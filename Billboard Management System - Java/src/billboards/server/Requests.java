package billboards.server;

import billboards.shared.Hash;
import billboards.shared.Schedule;
import billboards.shared.TCPMessage;
import billboards.shared.Utility;

import java.util.Arrays;

/**
 * Class containing all the requests that the requesthandler will be using
 * @author  Jack Nielsen, Ryan Harrold
 */
public class Requests {
    private static Database db = new Database();

    /**
     * static class containing session requests
     */
    static class session{
        /**
         * Log in request
         * @param message type,username,hashedpassword
         * @return
         */
        public static String login(String[] message){
            //check message for correct values
            if(message.length != 3){
                System.out.println("Incorrect values");
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
            }

            //fields utilised

            String username = message[1];
            String hashedPassword = message[2];

            String[] userData = db.get("user","username",username);
            System.out.println(username + " " + hashedPassword);

            //verify login and generate session token
            if(Authentication.verifyLogin(username,hashedPassword)){
                System.out.println("Successfully authenticated");
                String sessionToken = Session.createSessionToken();
                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS,userData[0],sessionToken);
            }
            return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,Authentication.authenticationStatus);
        }

        /**
         * Log out request
         * @param message type,token
         * @return
         */
        public static String logout(String[] message){
            //check message for correct values
            if(message.length != 2){
                System.out.println("Incorrect values");
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
            }

            String token = message[1];
            Session.expireSession(token);
            return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS);
        }
    }

    static class user{
        /**
         * Create user request
         * @param message type, username, hashedpassword, type
         * @return
         */
        public static String createUser(String[] message){
            try {
                //check message for correct values

                if(message.length != 5){
                    System.out.println("Incorrect values");
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
                }

                //fields utilised
                String id = message[1];
                String username = message[2];
                String hashedPassword = message[3];
                String token = message[4];
                String storedSalt  = Salt.generateSalt();
                String userFinalHash = Hash.getHash(hashedPassword + storedSalt);

                //verify session token
                if(!Session.verifySession(token)){
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Invalid session");
                }

                String[] values = {id,username,storedSalt,userFinalHash};

                //insert user row into database
                db.updateOrInsert("user","idx",id,values);
                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS);
            } catch (Exception e) {
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Error creating user");
            }
        }

        /**
         * delete user request
         * @param message type, user id, token
         * @return
         */
        public static String deleteUser(String[] message){
            try {
                //check message for correct values
                if(message.length != 3){
                    System.out.println("Incorrect values");
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
                }

                //fields utilised
                String userId = message[1];
                String token = message[2];

                //verify session token
                if(!Session.verifySession(token)){
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Invalid session");
                }
                //delete user & dependencies
                String[][] userBillboards = db.select("SELECT * FROM billboard where idx = '" + userId + "';");
                for(String[] s : userBillboards){
                    db.delete("schedule","billboardId",s[0]);
                }
                db.delete("permission","idx",userId);
                db.delete("billboard","idx",userId);
                db.delete("user","idx",userId);


                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS);
            } catch (Exception e) {
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Error deleting user");
            }
        }

        /**
         * Get user permissions request
         * @param message type, user id, token
         * @return
         */
        public static String getUserPermissions(String[] message){
            try {
                //check message for correct values
                if(message.length != 3){
                    System.out.println("Incorrect values");
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
                }

                //fields utilised
                String userId = message[1];
                String token = message[2];

                //verify session token
                if(!Session.verifySession(token)){
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Invalid session");
                }

                //get permissions
                String userPermissions[] = db.get("permission","idx",userId);
                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS,userPermissions[1],userPermissions[2],userPermissions[3],userPermissions[4]);
            } catch (Exception e) {
                e.printStackTrace();
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Error getting permissions");
            }
        }

        /**
         * Set user permissions request
         * @param message type, user id, true, false, true, false, token
         * @return
         */
        public static String setUserPermissions(String[] message){
            try {
                //check message for correct values
                if(message.length != 7){
                    System.out.println("Incorrect values");
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
                }

                //Utilised fields
                String id = message[1];
                String token = message[6];
                String[] permissions = {id, message[2],message[3],message[4],message[5]};

                //Verify session token
                if(!Session.verifySession(token)){
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Invalid session");
                }



                //update or insert user permissions
                db.updateOrInsert("permission","idx",id+"",permissions);
                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Error setting permissions");
            }
        }

        /**
         * Set user password request
         * @param message type, user id, hashed password, token
         * @return
         */
        public static String setUserPassword(String[] message){
            try {
                //check message for correct values
                if(message.length != 4){
                    System.out.println("Incorrect values");
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
                }

                //Utilised fields
                String userId = message[1];
                String hashedPass = message[2];
                String token = message[3];
                String[] userFields = db.get("user","idx",userId);
                String username = userFields[1];
                String salt = userFields[2];
                String finalHashed = Hash.getHash(hashedPass + salt);

                String[] updatedUserEntry = {userId,username,salt,finalHashed};

                //Verify session token
                if(!Session.verifySession(token)){
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Invalid session");
                }

                //update or insert user password
                db.updateOrInsert("user","idx",userId+"",updatedUserEntry);
                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS);
            } catch (Exception e) {
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Error setting user password");
            }
        }

        /**
         * Get users request
         * @param message type, token
         * @return
         */
        public static String getUserList(String[] message){
            try {
                //check message for correct values
                if(message.length != 2){
                    System.out.println("Incorrect values");
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
                }

                //Utilised fields
                String token = message[1];
                if(!Session.verifySession(token)){
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Invalid session");
                }

                //Get user id
                String[][] userData = db.select("" +
                        "SELECT user.idx,user.username,permission.createBillboards,permission.editBillboards,permission.scheduleBillboards,permission.editUsers FROM user\n" +
                        "JOIN permission\n" +
                        "ON user.idx = permission.idx\n" +
                        "GROUP BY user.idx;");

                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS,userData);
            } catch (Exception e) {
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Error setting permissions");
            }
        }
    }

    /**
     * Class containing billboard requests
     */
    static class billboard{
        /**
         * list billboards request
         * @param message type, token
         * @return a tcp message that sends a string[i][1] of the names of all the  billboards (string[i][0] = id, string [i][1] = name)
         */
        public static String listBillboards(String[] message){
            //check message for correct values
            if(message.length != 2){
                System.out.println("Incorrect values");
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
            }


            String token = message[1];

            if(!Session.verifySession((token))){
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "invalid session token");
            }


            String[][] billboards = db.select("SELECT * FROM billboard;");
            return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS, billboards);
        }

        /**
         * get billboard request
         * @param message type, billboardID, token
         * @return Either a Success response with the info of the billboard, or a fail response
         */
        public static String getBillboardInfo(String[] message){
            //check message for correct values
            if(message.length != 3){
                System.out.println("Incorrect values");
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
            }

            String name = message[1];

            String token = message[2];

            if(!Session.verifySession((token))){
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "invalid session token");
            }

            return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS,db.get("billboard", "name", name));
        }


        /**
         * @param message (TCPMessage.type, billboardId, billboard name, billboard data, session token)
         *
         * if billboardID is null, it will fail. so I guess the client must find what the next id is?
         *
         * @return Either Success or fail (and relevant messages)
         *
         */
        public static String createOrEditBillboard(String[] message){
            //check message for correct values
            if(message.length != 4){
                System.out.println("Incorrect values");
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
            }

            //get the info
            String id = message[1];
            String name = message[2];
            String data = message[3];

            String[] values = {id,name,data};

            //get the session token
            String token = message[4];

            //verify session token
            if(!Session.verifySession((token))){
               return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "invalid session token");
            }

            //if it already exists, update it. if it doesn't exist, create it
            if(db.updateOrInsert("billboard", "billboardId", id, values)){
                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS);
            }else{
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "failed to create table");
            }

        }

        /**
         * delete billboard request
         * @param message (type, billboard id, session token)
         * @return success or fail
         */
        public static String deleteBillboard(String[] message) {
            //check message for correct values
            try {
                if (message.length != 3) {
                    System.out.println("Incorrect values");
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "Incorrect values");
                }

                String id = message[1];
                String token = message[2];

                //verify session
                if (!Session.verifySession(token)) {
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "Invalid Session");
                }
                db.delete("Schedule","billboardId",id);
                db.delete("Billboard", "billboardId", id);
                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "Failed to delete billboard");
        }

        /**
         * schedule billboard request
         * @param message (type,billboardID, time(yyyy-mm-dd hh:mm:ss), duration , session)
         * @return success or fail
         */
        public static String scheduleBillboard(String[] message) {
            try {
                //check message for correct values
                if (message.length != 6) {
                    System.out.println("Incorrect values");
                    return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "Incorrect values");
                }

                //unpack message
                //Note: must be a better way of doing this...
                String scheduleId = message[1];
                String time = message[2];
                String duration = message[3];
                String billboardID = message[4];
                String token = message[5];

                //verify session
                if (!Session.verifySession(token)) {
                    TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "Invalid session token");
                }

                if (db.insert("schedule",scheduleId, time, duration, billboardID)) {
                    return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "Failed to insert into database");
        }


        /**
         * view schedule request
         * @param message type, token
         * @return 2D array schedule
         */
        public static String viewSchedule(String[] message){
            //check message for correct values
            if(message.length != 2){
                System.out.println("Incorrect values ");
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
            }

            String token = message[1];

            if(!Session.verifySession(token)){
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "Invalid session token");
            }

            //This returns all active billboard schedules in the order
            String[][] schedule = db.select("SELECT schedule.scheduleId,billboard.name,schedule.time,schedule.durationMins,billboard.billboardId\n" +
                    "FROM schedule\n" +
                    "JOIN billboard ON billboard.billboardId = schedule.billboardId\n" +
                    "WHERE time + INTERVAL durationMins MINUTE > NOW()\n" +
                    "ORDER BY time ASC;");
            return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS, schedule);
        }

        /**
         * remove schedule request
         * @param message type, scheduleId, token
         * @return success or fail
         */
        public static String removeSchedule(String[] message){
            //check message for correct values
            if(message.length != 3){
                System.out.println("Incorrect values");
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Incorrect values");
            }

            String id = message[1];
            String token = message[2];

            if(!Session.verifySession(token)){
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "Invalid Session Token");
            }

            if(db.delete("schedule", "scheduleId", id)){
                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS);
            }else{
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL, "Failed to delete schedule");
            }
        }
    }

    /**
     * static class containing viewer request
     */
    static class viewer{
        /**
         * Request the viewer makes every 15 seconds
         * @param message type
         * @return
         */
        public static String getBillboardContent(String[] message){
            try{
                String[][] activeSchedules = db.select("SELECT schedule.scheduleId,billboard.name,schedule.time,schedule.durationMins\n" +
                        "FROM schedule\n" +
                        "JOIN billboard ON billboard.billboardId = schedule.billboardId\n" +
                        "WHERE time + INTERVAL durationMins MINUTE > NOW()\n" +
                        "ORDER BY time ASC;");
                String scheduleId = Schedule.getCurrentlyScheduledId(activeSchedules);
                String billboardId = db.get("schedule","scheduleId",scheduleId)[3];
                String content = db.get("billboard","billboardId",billboardId)[2];

                return TCPMessage.build(TCPMessage.type.RESPONSE_SUCCESS, content);
            }
            catch(Exception e){
                e.printStackTrace();
                return TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Error retrieving billboard content");
            }
        }
    }
}
