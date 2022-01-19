package billboards.server;

import billboards.shared.Configurations;
import billboards.shared.TCPMessage;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
/**
 * Class containing the main server logic: initialise and listen
 * @author  Jack Nielsen, Ryan Harrold
 */
public class Server{
    private Connection connection;
    private int Port;
    private Configurations conf = new Configurations(MethodHandles.lookup().lookupClass().getPackage());
    private ServerSocket server;

    private DataOutputStream out;
    private DataInputStream in;

    /**
     * Main method
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.initialise();
        server.listen();
    }

    public void run(){
        try {
            initialise();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise database and connection
     * @throws IOException
     * @throws SQLException
     */
    private void initialise() throws IOException, SQLException {
        //initialise database
        Connection connection = DatabaseConnection.getInstance();

        System.out.println(DatabaseConnection.freshTablesOnCreation ? "Creating a clean database.." : "Checking/creating database exists..");

        Statement statement = connection.createStatement();
        try {
            //Create schema
            for (String query : DatabaseConnection.SCHEMA_QUERIES) {
                if (!query.equals("")) {
                    statement.execute(query);
                }
            }
            //Insert test data, this will only work if both booleans are true, to avoid trying to insert test data into the previous db schema
            if (DatabaseConnection.freshTablesOnCreation && DatabaseConnection.insertTestData) {
                print("Inserting test data");
                for (String query : DatabaseConnection.INSERT_QUERIES) {
                    statement.execute(query);
                }
            }
            print("Database ready");
        }catch (Error e) {
            print("Couldnt create table for reason: " + e);
        }

        //Set port according to config file
        this.Port = conf.getPort();

        //initialise serverSocket
        this.server = new ServerSocket(Port);
    }


    /**
     * Main server method, listens for connections, upon receiving connection it uses the request handler to return data and then closes the connection with the client
     * @throws Exception
     */
    private void listen() throws Exception {
        try {
            while (true) {
                Socket client = server.accept(); //listens on the port until a connection is made

                print("Client connected: " + client.getInetAddress());

                InputStream inFromClient = client.getInputStream();
                OutputStream outToClient = client.getOutputStream();

                in = new DataInputStream(inFromClient);
                out = new DataOutputStream(outToClient);

                String request = receiveFromClient();
                String response = requestHandler(request);
                sendToClient(response);
                in.close();
                out.close();
                client.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method for receiving from client
     * @return TCPMessage from client
     */
    private String receiveFromClient(){
        try {
            return in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method for sending to client
     * @param msg TCPMessage
     */
    private void sendToClient(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main request handling method
     * Identifies request and then directs to according request method, returns a String to be sent back to client
     * @param request TCPMessage
     * @return
     */
    private String requestHandler(String request) {

        String[] requestFields = TCPMessage.receive(request);

        int requestType = Integer.parseInt(requestFields[0]);

        String response = TCPMessage.build(TCPMessage.type.RESPONSE_FAIL,"Request handler was unable to identify request");
        //removethislater
        String forDebug = response;

        if(requestType == TCPMessage.type.REQUEST_LOGIN){
            response = Requests.session.login(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_LOGOUT){
            response = Requests.session.logout(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_CREATE_USER){
            response = Requests.user.createUser(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_DELETE_USER){
            response = Requests.user.deleteUser(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_USER_PERMISSIONS){
            response = Requests.user.getUserPermissions(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_SET_PERMISSIONS){
            response = Requests.user.setUserPermissions(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_SET_USER_PASSWORD){
            response = Requests.user.setUserPassword(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_LIST_USERS){
            response = Requests.user.getUserList(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_CREATE_OR_EDIT_BILLBOARD){
            response = Requests.billboard.createOrEditBillboard(requestFields);
        } else if(requestType == TCPMessage.type.REQUEST_GET_BILLBOARD){
            response = Requests.billboard.getBillboardInfo(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_LIST_BILLBOARDS){
            response = Requests.billboard.listBillboards(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_DELETE_BILLBOARD){
            response = Requests.billboard.deleteBillboard(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_VIEW_SCHEDULE){
            response = Requests.billboard.viewSchedule(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_SCHEDULE){
            response = Requests.billboard.scheduleBillboard(requestFields);
        }else if(requestType == TCPMessage.type.REQUEST_REMOVE_SCHEDULE){
            response = Requests.billboard.removeSchedule(requestFields);
        } else if(requestType == TCPMessage.type.REQUEST_VIEWER_REQUEST){
            response = Requests.viewer.getBillboardContent(requestFields);
        }

        //removethislater
        if(response.equals(forDebug)){
            print(forDebug);
        }
        return response;
    }

    private void print(String s) {
        System.out.println(s);
    }
}