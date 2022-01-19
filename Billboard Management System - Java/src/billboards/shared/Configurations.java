package billboards.shared;

import billboards.server.DatabaseConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
/**
 * This class is used for obtaining package specific configurations
 * @author  Jack Nielsen
 */
public class Configurations {

    String path;

    /**
     * Class constructor, takes method handles parameter which finds the package relative path in the out folder
     * @param pkg
     */
    public Configurations(Package pkg) {
        this.path = pathToProperties(pkg);
    }


    //<editor-fold defaultstate="collapsed" desc="Public methods">

    /**
     * Gets address from config file
     * @return
     */
    public String getAddress() {
        Object address = getProperties().get("address");
        return address.toString();
    }

    /**
     * gets port from config file
     * @return
     */
    public int getPort() {
        Object port = getProperties().get("port");
        return Integer.parseInt(port+"");
    }

    /**
     * Sets address in config file
     * @param address
     */
    public void setAddress(String address) {
        if (!path.contains("server")) {
            setProperties("address", address);
        } else {
            System.out.println("The server does not require an address");
        }
    }

    /**
     * Sets port in config file
     * @param port
     */
    public void setPort(int port) {
        setProperties("port", port + "");
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">

    /**
     * Uses Properties object to retrieve properties in file
     * @return
     */
    private Properties getProperties() {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Uses Properties object to set properties in file
     * @param key
     * @param value
     */
    private void setProperties(String key, String value) {
        Properties prop = getProperties();
        prop.setProperty(key, value);
        try {
            prop.store(new FileWriter(new File(path)), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves relative path to config file
     * @param currentPackage
     * @return
     */
    private String pathToProperties(Package currentPackage) {
        String packagePath = currentPackage.getName().replace(".", "/");
        return DatabaseConnection.class.getClassLoader().getResource(packagePath + "/config.properties").getPath();
    }
    // </editor-fold>
}
