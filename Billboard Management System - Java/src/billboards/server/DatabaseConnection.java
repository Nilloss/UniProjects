package billboards.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * This class is used for communication with the database from within the server
 * @author  Jack Mcsweeney,Jack Nielsen
 */
public class DatabaseConnection {


    private static Connection instance = null;

    //Boolean values here for creating fresh tables on creation and test data
    public static final boolean freshTablesOnCreation = true;
    public static final boolean insertTestData = true;


    /**
     * Constructor intializes the connection.
     */
    private DatabaseConnection() {
        try {

            // specify the data source, username and password
            String url = getProp("jdbc.url");
            String username = getProp("jdbc.username");
            String password = getProp("jdbc.password");
            String schema = getProp("jdbc.schema");

            // get a connection
            instance = DriverManager.getConnection(url + "/" + schema, username,
                    password);
        } catch (SQLException sqle) {
            System.err.println(sqle);
        }

    }
    /**
     * Retrieves JDBC properties from db.props
     * @param name
     * @return
     */
    private static String getProp(String name) {
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(DatabaseConnection.class.getClassLoader().getResource("billboards/server/db.props").getPath());
            props.load(in);
            in.close();

            // return value
            return props.getProperty(name);
        } catch (FileNotFoundException fnfe) {
            System.err.println(fnfe);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Provides global access to the singleton instance of the UrlSet.
     * @return a handle to the singleton instance of the UrlSet.
     */
    public static Connection getInstance() {
        if (instance == null) {
            new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Array containing schema queries to be executed upon initialisation
     */
    public static String[] SCHEMA_QUERIES = {

            freshTablesOnCreation ? "DROP TABLE IF EXISTS session;" : "",
            freshTablesOnCreation ? "DROP TABLE IF EXISTS schedule;" : "",
            freshTablesOnCreation ? "DROP TABLE IF EXISTS permission;" : "",
            freshTablesOnCreation ? "DROP TABLE IF EXISTS billboard;" : "",
            freshTablesOnCreation ? "DROP TABLE IF EXISTS user;" : "",

            "CREATE TABLE IF NOT EXISTS user (\n" +
                    "idx VARCHAR(50) PRIMARY KEY NOT NULL UNIQUE,\n" +
                    "username VARCHAR(30),\n" +
                    "salt VARCHAR(255),\n" +
                    "hash VARCHAR(255));",
            "CREATE TABLE IF NOT EXISTS permission (\n" +
                    "idx VARCHAR(50),\n" +
                    "createBillboards bool,\n" +
                    "editBillboards bool,\n" +
                    "scheduleBillboards bool,\n" +
                    "editUsers bool,\n" +
                    "FOREIGN KEY (idx) REFERENCES user (idx)\n" +
                    ");",
            "CREATE TABLE IF NOT EXISTS billboard (\n" +
                    "billboardId VARCHAR(50) PRIMARY KEY NOT NULL UNIQUE,\n" +
                    "name VARCHAR(30),\n" +
                    "content TEXT(1000),\n" +
                    "idx VARCHAR(50),\n" +
                    "FOREIGN KEY (idx) REFERENCES user (idx)\n" +
                    ");",
            "CREATE TABLE IF NOT EXISTS schedule (\n" +
                    "scheduleId VARCHAR(50) PRIMARY KEY NOT NULL UNIQUE,\n" +
                    "time dateTime,\n" +
                    "durationMins INTEGER,\n" +
                    "billboardId VARCHAR(50),\n" +
                    "FOREIGN KEY (billboardId) REFERENCES billboard (billboardId)\n" +
                    ");",
            "CREATE TABLE IF NOT EXISTS session (\n" +
                    "token VARCHAR(255)\n" +
                    ");"
    };

    /**
     * Array containing insert queries upon initialisation
     */
    public static String[] INSERT_QUERIES = {
            "INSERT INTO user VALUES ('133fdac3-ddfd-45d5-af68-337fad05a25a','admin','lYsXNXZiqbL1a/q0ElJP2DhZAamF2vZD5/r2RsL+5F8=','e22f0d67a329d70661d10dbf8810bd16');",
            "INSERT INTO user VALUES ('78cb72c9-c320-4529-87f4-ebce21cd3537','user','w5fg3m+Fy//J/px7kPjDv9qmnCK/7CxawBuo7hXrGFI=','4b57cb786873a6729bb88cf38a5659b4');",
            "INSERT INTO permission VALUES ('133fdac3-ddfd-45d5-af68-337fad05a25a',true,true,true,true);",
            "INSERT INTO permission VALUES ('78cb72c9-c320-4529-87f4-ebce21cd3537',true,false,true,false);",
            "INSERT INTO billboard VALUES ('8fd744eb-9fc3-4a47-b45c-1656284eb4f5','billboard1','<billboard>\n" +
                    "<message>Billboard 1 content</message>\n" +
                    "</billboard>','133fdac3-ddfd-45d5-af68-337fad05a25a');",
            "INSERT INTO billboard VALUES ('964b22c1-94d1-40d1-9cf8-c4d6abc9b302','billboard2','<billboard>\n" +
                    "<message>Billboard 2 content</message>\n" +
                    "</billboard>','133fdac3-ddfd-45d5-af68-337fad05a25a');",
            "INSERT INTO billboard VALUES ('f51cd516-423a-49b0-ad65-75212ef8456e','billboard3','<billboard>\n" +
                    "<message>Billboard 3 content</message>\n" +
                    "</billboard>','133fdac3-ddfd-45d5-af68-337fad05a25a');",
            "INSERT INTO billboard VALUES ('954d02dd-88fc-486a-88a1-27a7124be425','billboard4','<billboard>\n" +
                    "<message>Billboard 4 content</message>\n" +
                    "</billboard>','78cb72c9-c320-4529-87f4-ebce21cd3537');",
            "INSERT INTO billboard VALUES ('209ceb23-e9ad-426c-b57e-9f3c532e8625','billboard5','<billboard>\n" +
                    "<message>Billboard 5 content</message>\n" +
                    "</billboard>','78cb72c9-c320-4529-87f4-ebce21cd3537');",
            "INSERT INTO billboard VALUES ('58c173a1-c9bb-4ba0-829f-26047829bdef','billboard6','<billboard>\n" +
                    "<message>Billboard 6 content</message>\n" +
                    "</billboard>','78cb72c9-c320-4529-87f4-ebce21cd3537');",
            "INSERT INTO schedule VALUES ('49195e43-adfe-4336-adf0-dc6a26dcade1','" + LocalDateTime.now() + "',5,'58c173a1-c9bb-4ba0-829f-26047829bdef');",
            "INSERT INTO schedule VALUES ('6731898b-efea-41b4-adb2-3doj3dojdd3n','" + LocalDateTime.now().plusMinutes(1) + "',2,'8fd744eb-9fc3-4a47-b45c-1656284eb4f5');",
            "INSERT INTO schedule VALUES ('d98a4e9a-faf2-4a68-afd4-f47126a4b35a','" + LocalDateTime.now().plusMinutes(6) + "',5,'964b22c1-94d1-40d1-9cf8-c4d6abc9b302');",
            "INSERT INTO schedule VALUES ('b7f45ce2-2b02-4385-8887-ac8ef8b73480','" + LocalDateTime.now().plusMinutes(10) + "',3,'954d02dd-88fc-486a-88a1-27a7124be425');",
            "INSERT INTO schedule VALUES ('f741437f-8b2e-4bf3-b478-1715d6a8f798','" + LocalDateTime.now().plusMinutes(12) + "',10,'209ceb23-e9ad-426c-b57e-9f3c532e8625');",
            "INSERT INTO schedule VALUES ('ba5ba14f-f1fd-4f94-ab56-ccc0f57f76b5','" + LocalDateTime.now().plusMinutes(23) + "',20,'f51cd516-423a-49b0-ad65-75212ef8456e');",
            "INSERT INTO session VALUES ('[B@64a294a6');",
            "INSERT INTO session VALUES ('[B@3d82c5f3');",
            "INSERT INTO session VALUES ('[B@2b05039f');",
            "INSERT INTO session VALUES ('[B@61e717c2');",
            "SET GLOBAL event_scheduler = ON;"
    };

}


