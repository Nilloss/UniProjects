package billboards.controlPanel;

import billboards.shared.Configurations;
import billboards.shared.TCPMessage;
import billboards.shared.Utility;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.Socket;
/**
 * This is the TCP logic for the control panel to communicate with the server, is used in conjunction with TCPMessage
 * @author  Jack Nielsen
 */
public class ServerCommunication {

    private static Configurations conf = new Configurations(MethodHandles.lookup().lookupClass().getPackage());
    private static Socket server;
    private static DataOutputStream out;
    private static DataInputStream in;

    /**
     * Main method, creates connection and then closes it after receiving the response
     * @param toServer TCPMessage
     * @return
     */
    public static String getResponse(String toServer)  {
        try{
            server = new Socket(conf.getAddress(),conf.getPort());
            //write to socket using ObjectOutputStream
            out = new DataOutputStream(server.getOutputStream());

            out.writeUTF(toServer);
            //read the server response message
            in = new DataInputStream(server.getInputStream());
            String response = in.readUTF();

            closeConn();
            Thread.sleep(100);
            return response;
        }
        catch(Exception e){
            return "Error communicating with server";
        }
    }

    /**
     * Close connection with server
     * @throws IOException
     */
    static void closeConn() throws IOException {
        in.close();
        out.close();
    }

    public static void main(String... args){
        String message = TCPMessage.build(TCPMessage.type.REQUEST_LIST_BILLBOARDS, "[B@64a294a6");
        String response = ServerCommunication.getResponse(message);
        String[][] fields = TCPMessage.receiveList(response);
        Utility.print2dArray(fields);
    }
}
