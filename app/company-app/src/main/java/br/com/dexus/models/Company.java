/**
 * Created by Fabio Oliveira Costa 
 */
package br.com.dexus.models;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import java.util.HashMap;

public class Company extends AbstractModel{

    public Company(Connection conn) throws SQLException{
        super(conn);
    }
    public  Company(Connection conn, JSONObject json){
        super(conn,json);
    }

    public Company(Connection conn, Integer id) throws SQLException{
        super(conn,id);
    }
    public  Company(Connection conn, int id,JSONObject json)  throws SQLException{
        super(conn,id, json);
    }
    protected String _getTable(){
        return "company";
    }
    
    protected  HashMap<String,DataBaseField>  _getFields(){
        HashMap<String,DataBaseField> fields = new HashMap<String,DataBaseField>();
        fields.put("id",new DataBaseField("id","int",false,true));
        fields.put("name",new DataBaseField("name","string",true));
        fields.put("city",new DataBaseField("city","string",true));
        fields.put("country",new DataBaseField("country","string",true));
        fields.put("address",new DataBaseField("address","string",true));
        fields.put("email",new DataBaseField("email","string"));
        fields.put("phone",new DataBaseField("phone","string"));
        return fields;
    }


}