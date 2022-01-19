package billboards.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * This class contains the database crud methods
 * @author  Jack Nielsen
 */
public class Database {

    /**
     * Retrieves an entry from the database
     * @param table Table to retrieve data from
     * @param columnName name of column in where clause
     * @param value value in the column of the where clause
     * @return
     */
    public String[] get(String table, String columnName, String value) {
        try{
            Statement statement = DatabaseConnection.getInstance().createStatement();
            String query = "SELECT * FROM " + table + " WHERE " + columnName + " = " + "'" + value + "';";
            ResultSet rs = statement.executeQuery(query);
            rs.first();
            int columns = rs.getMetaData().getColumnCount();
            ArrayList<String> values = new ArrayList<>();
            for(int i = 0; i < columns; i ++){
                values.add(rs.getString(i+1));
            }
            String[] stringArray = Arrays.copyOf(values.toArray(), values.size(), String[].class);
            return stringArray;
        }catch(SQLException e){
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * Inserts an entry into the database
     * @param table table to insert entry into
     * @param values values to insert
     * @return
     */
    public boolean insert(String table, String... values) {
        try {
            String query = "INSERT INTO ";
            query = query + table + " VALUES (";
            for (String s : values) {
                query += "'" + s + "',";
            }
            query = query.substring(0, query.length() - 1) + ");";
            Statement statement = DatabaseConnection.getInstance().createStatement();
            int rowsInserted = statement.executeUpdate(query);
            if (rowsInserted > 0) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Updates an entry in the database
     * @param table table to update
     * @param columnName column name in where clause
     * @param value value in where clause
     * @param values values to update with
     * @return
     */
    public boolean update(String table,String columnName, String value, String[] values) {
        try {
            ResultSetMetaData tableData = getTableData(table);

            String sql = "UPDATE " + table + " SET ";
            for(int i = 0; i < values.length; i ++){
                sql += tableData.getColumnName(i+1) + "='" + values[i] +"', ";
            }
            sql = sql.substring(0,sql.length()-2) + " WHERE " + columnName + " = '" + value + "';";
            PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement(sql);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException throwables) {
        }
        return false;
    }

    /**
     * Checks if entry exists, if so it will update it, otherwise insert new entry
     * @param table table to update
     * @param columnName column name in where clause
     * @param value value in where clause
     * @param values values to insert
     * @return
     */
    public boolean updateOrInsert(String table, String columnName, String value, String[] values){
        if(update(table,columnName,value,values)){
            return true;
        }
        return insert(table,values);
    }

    /**
     * Delete entry from database
     * @param table table to delete from
     * @param columnName column name in where clause
     * @param value value in where clause
     * @return
     */
    public boolean delete(String table,String columnName, String value) {
        try {
            String sql = "DELETE FROM " + table + " WHERE + " + columnName + " = '" + value + "';";

            PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement(sql);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * Method for executing any sql query on the database
     * @param query
     * @return
     */
    public boolean execute(String query){
        try {
            PreparedStatement statement = DatabaseConnection.getInstance().prepareStatement(query);
            return statement.execute(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * Select query, returns a 2d array
     * @param query
     * @return
     */
    public String[][] select(String query) {
        try {
            Statement statement = DatabaseConnection.getInstance().createStatement();
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();

            int x = rsmd.getColumnCount();
            int y = countRows(rs);
            String[][] fields = new String[y][x];

            for (int i = 0; i < y; i ++){
                rs.next();
                for (int k = 0; k < x; k++) {
                    fields[i][k] = rs.getString(k+1);
                }
            }
            return fields;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    //<editor-fold default-state="collapsed" desc="Private methods">

    /**
     * Gets table meta data for assistance in building 2d arrays from the JDBC object
     * @param table table to retrieve data from
     * @return
     * @throws SQLException
     */
    private ResultSetMetaData getTableData(String table) throws SQLException {
        return DatabaseConnection.getInstance().createStatement().executeQuery("SELECT * FROM " + table + ";").getMetaData();
    }

    /**
     * counts rows from a result set
     * @param r
     * @return
     * @throws SQLException
     */
    private int countRows(ResultSet r) throws SQLException {
        int count = 0;
        while(true){
            try {
                if (!r.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            count++;
        }
        r.beforeFirst();
        return count;
    }
    //</editor-fold>
}
