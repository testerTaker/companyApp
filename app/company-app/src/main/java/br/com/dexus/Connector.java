/**
 * Created by Fabio Oliveira Costa 
 * 
 *  Connector factory for SQLite
 */
package br.com.dexus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Connector{
    public static Connection getConnection() 
    {
        return getConnection("/app/user/base.db");
    }
    public static Connection getConnection(String dbName) 
    {

        try{
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
            Connection connection = null;
            connection = DriverManager.getConnection("jdbc:sqlite:"+dbName);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); 
            return connection;
        }
        catch(Exception e){
            return null;
        }
    }
}