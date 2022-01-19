package billboards.server;


import billboards.controlPanel.ServerCommunication;
import billboards.shared.Hash;
import billboards.shared.TCPMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.UUID;

/**
 * JUnit tests which test all the components of the server
 */
public class serverTests {

    //<editor-fold default-state="collapsed" desc="Database">
    Database db = new Database();

    /**
     * Test database connection
     */
    @Test
    public void TestDbConnection(){
        try {
            assertFalse(DatabaseConnection.getInstance().isClosed());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    /**
     * Test database methods
     */
    @Test
    public void TestDbGet(){
        //test get
        String[] entry = db.get("billboard","name","billboard5");
        assertEquals(entry[1],"billboard5");
    }

    @Test
    public void TestDbInsert(){
        String randomId = UUID.randomUUID().toString();
        assertTrue(db.insert("billboard",randomId,"billboard7","purple"));
    }

    @Test
    public void TestDbUpdate(){
        String[] row = db.get("billboard","name","billboard7");
        row[2] = "green";
        assertTrue(db.updateOrInsert("billboard","name","billboard7",row));
    }

    @Test
    public void TestDbDelete(){
        String randomId = UUID.randomUUID().toString();
        db.insert("billboard",randomId,"billboard8","fordeletion");
        assertTrue(db.delete("billboard","name","billboard8"));
    }

    @Test
    public void TestDbSelect(){
        String[][] tableData = db.select("SELECT * FROM billboard;");
        assertTrue(tableData.length > 0);
    }

    @Test
    public void TestExecuteQuery(){
        assertTrue(db.execute("SELECT * FROM billboard;"));
    }
    //</editor-fold>

    //<editor-fold default-state="collapsed" desc="Requests">


    @BeforeAll
    public static void TestRunServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Server server = new Server();
                server.run();
            }
        }).start();
    }


    /**
     * Tests a user request
     * (Not a proper deployment test since it uses ServerCommunication in the control panel package)
     * Similates logging in with admin user and pass
     */
    @Test
    public void TestUserRequest(){
        String message = TCPMessage.build(TCPMessage.type.REQUEST_LOGIN,"admin",Hash.getHash("admin"));
        String response = ServerCommunication.getResponse(message);
        String[] fields = TCPMessage.receive(response);
        int responseId= Integer.parseInt(fields[0]);
        assertTrue(responseId == TCPMessage.type.RESPONSE_SUCCESS);
    }

    /**
     * Tests a billboard request
     * If the server successfully returns the billboard
     * the billboards name will match 'billboardId'
     */
    @Test
    public void TestBillboardRequest(){
        String message = TCPMessage.build(TCPMessage.type.REQUEST_GET_BILLBOARD,"58c173a1-c9bb-4ba0-829f-26047829bdef","[B@64a294a6");
        String response = ServerCommunication.getResponse(message);
        String[] fields = TCPMessage.receive(response);
        String billboardId = fields[1];
        assertTrue(billboardId.equals("billboard6"));
    }

    /**
     * Tests the viewer request
     * If server returns billboard content it will have a success identifier
     */
    @Test
    public void TestViewerRequest(){
        String message = TCPMessage.build(TCPMessage.type.REQUEST_VIEWER_REQUEST);
        String response = ServerCommunication.getResponse(message);
        String[] fields = TCPMessage.receive(response);
        int responseId= Integer.parseInt(fields[0]);
        assertTrue(responseId == TCPMessage.type.RESPONSE_SUCCESS);
    }

    //</editor-fold>

    //<editor-fold default-state="collapsed" desc="Authentication">

    /**
     * Tests correct salt generation by checking that first generation doesnt match the second (So its random)
     */
    @Test
    public void TestCorrectSaltGeneration(){
        String firstGeneration = Salt.generateSalt();
        String secondGeneration = Salt.generateSalt();
        assertTrue(!firstGeneration.equals(secondGeneration));
    }

    /**
     * Tests hash generation by checking if initial string doesn't match hashed version
     */
    @Test
    public void TestHashGeneration(){
        String password = "password123";
        String hashed = Hash.getHash(password);
        assertTrue(!password.equals(hashed));
    }

    /**
     * This test replicates the authentication process
     * and tests it by making sure that the same password
     * will authenticate after the salt+hash process
     */
    @Test
    public void TestReplicateAuthenticationProcess(){
        //Create user
        String username = "username";
        String password = "password123";
        String salt = Salt.generateSalt();
        String hashedPassword = Hash.getHash(password);
        String hashPlusSalt = salt + hashedPassword;

        //This is stored in db
        String finalHashed = Hash.getHash(hashPlusSalt);

        //Replicating login process
        String getUserSalt = salt;
        String hashedPassFromCP = hashedPassword;
        String hashPlusSalt2 = getUserSalt + hashedPassFromCP;
        String finalHashed2 = Hash.getHash(hashPlusSalt2);

        //Check what has been provided by the cp matches the hash in the db
        assertEquals(finalHashed,finalHashed2);
    }


    //</editor-fold>
}
