package billboards.shared;


import billboards.controlPanel.ServerCommunication;

import java.util.UUID;

/**
 * The purpose of this class is to have a unified way of building/receiving the TCP messages
 * between the server, cp & viewer
 * @author  Jack Nielsen
 */
public class TCPMessage {

    /**
     * Use this to send simple rows of data or simple requests
     * @param type TCPMessage.type (message identifier)
     * @param fields Dynamic String array which creates csv string
     * @return csv String result
     */
    public static String build(int type, String... fields){
        return type + "," + String.join(",",fields);
    }

    /**
     * Use this to build TCPMessages with table data (from a db.select)
     * @param type TCPMessage.type (message identifier)
     * @param array 2D array for sending lists
     * @return A csv String as such "type(integer),user1-password1-true-true-true-false,user2-password2-true-true-true-false,user3-password3-true-true-true-false"
     */
    public static String build(int type, String[][] array){
        String[] rows = new String[array.length];
        for(int i = 0; i < array.length; i ++){
            rows[i] = String.join("~",array[i]);
        }
        return build(type,rows);
    }

    /**
     * Use this when receiving single rows or simple requests
     * @param csv Example: "type(integer),username,password,token"
     * @return String array for easy access to the individual fields
     */
    public static String[] receive(String csv){
        String[] a = csv.split(",");

        return a;
    }

    /**
     * This is pretty much for two main requests such as getting a list of billboards/schedules or users and permissions
     * @param csv when expecting a table/list, pass the received csv as the parameter
     * @return A 2d array for easy usage containing table data
     */
    public static String[][] receiveList(String csv) {
        String[] a = receive(csv);
        if(a.length == 1){
            return null;
        }
        int y = a.length - 1;
        int x = a[1].split("~").length;
        String[][] array2d = new String[y][x];
        for (int i = 0; i < y; i++) {//starting at i = 1 to bypass the identifier
            String[] row = a[i+1].split("~");

            for (int k = 0; k < row.length; k++) {
                array2d[i][k] = row[k];
            }
        }
        return array2d;
    }

    /**
     * static class containing request values to be used in the builder
     */
    public final class type{
        public static final int REQUEST_LOGIN = 0;
        public static final int REQUEST_LOGOUT = 1;
        public static final int REQUEST_CREATE_USER = 2;
        public static final int REQUEST_DELETE_USER = 3;
        public static final int REQUEST_USER_PERMISSIONS = 4;
        public static final int REQUEST_SET_PERMISSIONS = 5;
        public static final int REQUEST_SET_USER_PASSWORD = 6;
        public static final int REQUEST_LIST_USERS = 7;
        public static final int REQUEST_CREATE_OR_EDIT_BILLBOARD = 8;
        public static final int REQUEST_GET_BILLBOARD = 9;
        public static final int REQUEST_LIST_BILLBOARDS = 10;
        public static final int REQUEST_DELETE_BILLBOARD = 11;
        public static final int REQUEST_VIEW_SCHEDULE = 12;
        public static final int REQUEST_SCHEDULE = 13;
        public static final int REQUEST_REMOVE_SCHEDULE = 14;
        public static final int REQUEST_VIEWER_REQUEST = 15;
        public static final int RESPONSE_SUCCESS = 1000;
        public static final int RESPONSE_FAIL = 1001;
    }


    /**
     * EXAMPLE IMPLEMENTATIONS
     */
    private void implementationExamples(){
        //The key things to note here is when you're expecting a 2dArray or table, use TCPMessage.receiveList
        //Know what to send and know what to expect back, below is a send/receive table you can use as a guide

        //                                -- Control Panel requests --
        //	- Login request                 s: type, username,hashedpassword                r: [success,id,token] or [fail,error msg]
        //	- List billboards	            s: type, token	                                r: [success,2dArray] or [fail,error msg]
        //	- Get billboard info	        s: type, billboard id, token 	                r: [success,name,content] or [fail,error msg]
        //	- Create or edit billboard      s: type, billboard id,billboard name,content,creator, token	r: [success,name,content] or [fail,error msg]
        //	- Delete billboard	            s: type, billboard id, token		            r: [success] or [fail,error msg]
        //	- View schedule	                s: type, token  		                        r: [success,2dArray] or [fail,error msg]
        //	- Schedule billboard            s: type, schedule id, time, duration, billboard id,token    r: [success] or [fail,error msg]
        //	- Remove billboard from sched   s: type, schedule id, token                    r: [success] or [fail,error msg]
        //	- List users 	                s: type, token		                            r: [success,2dArray] or [fail,error msg]
        //	- Create user	                s: type, id, username, hashedpassword,token		    r: [success,token] or [fail, error msg]
        //	- Get user permissions          s: type, user id, token 	                    r: [success,true,false,true,false] or [fail, error msg]
        //	- Set user permissions          s: type, user id, true,false,true,false, token	r: [success] or [fail, error msg]
        //	- set user password             s: type, user id, hashedpassword, token	        r: [success] or [fail, error msg]
        //	- delete user	                s: type, user id, token		                    r: [success] or [fail, error msg]
        //	- log out		                s: type, token		                            r: [success]

        //                               -- Viewer requests
        //  - getView                       s: type                                         r: [success,content] or [fail,error msg]


        //Create user
        String userId = UUID.randomUUID().toString();
        String createUserMessage = TCPMessage.build(type.REQUEST_CREATE_USER,userId,"Superman","Kryptonite123","(token)d903jd0hjdj");
        String create_user_response = ServerCommunication.getResponse(createUserMessage);
        String[] create_user_fields = TCPMessage.receive(create_user_response);

        if(Integer.parseInt(create_user_fields[0]) == type.RESPONSE_FAIL){ //<-- How to check if your response failed and get error message
            System.out.println(create_user_fields[1]);
        }

        //Create or edit billboard
        String billboardId = UUID.randomUUID().toString();
        String token = "getCurrentSessionToken();";
        String xmlString = "" +
                "  <book category=\"children\">\n" +
                "    <title>Harry Potter</title>\n" +
                "    <author>J K. Rowling</author>\n" +
                "    <year>2005</year>\n" +
                "    <price>29.99</price>\n" +
                "  </book>";
        String createBillBoardMessage = TCPMessage.build(type.REQUEST_CREATE_OR_EDIT_BILLBOARD,billboardId,xmlString,token);
        String create_billboard_response = ServerCommunication.getResponse(createBillBoardMessage);
        String[] create_billboard_fields = TCPMessage.receive(create_billboard_response);

        //View schedule
        String listSchedulesMessage = TCPMessage.build(type.REQUEST_VIEW_SCHEDULE,token);
        String list_schedule_response = ServerCommunication.getResponse(listSchedulesMessage);
        String[][] list_schedule_fields = TCPMessage.receiveList(list_schedule_response); // Ready to use in table

        //Logging in
        String loginMessage = TCPMessage.build(type.REQUEST_LOGIN,"Superman","Kryptonite123");
        String login_response = ServerCommunication.getResponse(loginMessage);
        String[] login_response_fields = TCPMessage.receive(login_response);

    }
}
