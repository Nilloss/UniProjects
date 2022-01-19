package billboards.viewer;

import billboards.shared.Configurations;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.Socket;

/**
 * handles connections between the client (viewer) and the server
 */
public class ClientConnection {
    Configurations conf = new Configurations(MethodHandles.lookup().lookupClass().getPackage());

    int port = conf.getPort(); //default port, TODO: get this from a properties file?  <-- updated by nilloss

    private String serverName = "localhost"; //default
    private Socket client;
    DataOutputStream out;
    DataInputStream in;

    /**
     * connects to the server
     * TODO: overload this so that you can choose your own ports
     */
    public void connectToServer(){
        try {
            client = new Socket(serverName, port);

            OutputStream outToServer = client. getOutputStream();
            InputStream inFromServer = client.getInputStream();

            out = new DataOutputStream(outToServer);
            in = new DataInputStream(inFromServer);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void writeToServer(String msg){

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            System.err.println("tried to Send " + msg + " and it failed!");
            e.printStackTrace();
        }

    }

    public String receiveFromServer(){
        try {
            return in.readUTF();
        } catch (IOException e) {
            System.err.println("Tried to receive from server and it failed");
            e.printStackTrace();
        }
        return null; //if an exception occurs
    }


    public void closeConnection(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //here just to test networking
    public static void main(String[] args){
        ClientConnection con = new ClientConnection();
        con.connectToServer();
        con.writeToServer("Hello!");
        System.out.println(con.receiveFromServer());
        con.closeConnection();
    }


}
