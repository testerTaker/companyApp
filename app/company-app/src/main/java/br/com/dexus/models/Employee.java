/**
 * Created by Fabio Oliveira Costa 
 */
package br.com.dexus.models;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.function.Function;
import java.util.HashMap;
import org.json.simple.JSONArray;

public class Employee extends AbstractModel{

    public Employee(Connection conn) throws SQLException{
        super(conn);
    }
    public  Employee(Connection conn, JSONObject json){
        super(conn,json);
    }

    public Employee(Connection conn, Integer id) throws SQLException{
        super(conn,id);
    }
    public  Employee(Connection conn, int id,JSONObject json)  throws SQLException{
        super(conn,id, json);
    }
    protected String _getTable(){
        return "employee";
    }
    
    protected  HashMap<String,DataBaseField>  _getFields(){
        HashMap<String,DataBaseField> fields = new HashMap<String,DataBaseField>();
        fields.put("id",new DataBaseField("id","int",false,true));
        fields.put("name",new DataBaseField("name","string",true));
        fields.put("email",new DataBaseField("email","string",true));
        fields.put("company_id",new DataBaseField("company_id","int",true));
        fields.put("is_owner",new DataBaseField("is_owner","int"));
        return fields;
    }
    protected boolean _checkIfCompanyExists() throws SQLException {
        PreparedStatement stmt = _conn.prepareStatement("select id from company WHERE id=?");
        stmt.setInt(1,(Integer) _values.get("company_id"));
        ResultSet rs = stmt.executeQuery();
        if(!rs.next()){
            return false;
        }
        return true;
    }

    public  boolean isValid(){
        if(!super.isValid()){
            return false;
        }
        try{
            return this._checkIfCompanyExists();

        }
        catch(SQLException e ){
            return false;
        }
    }
    public JSONArray listAll() throws SQLException{
       throw new UnsupportedOperationException("A company id is always required to list") ;
    }

    public JSONArray listAll(Integer companyId) throws SQLException{
        return listAll(companyId,null);
    }

    public JSONArray listAll(Integer companyId, Boolean isOwner) throws SQLException{
        JSONArray list = new JSONArray();
        String query = "select * from "+_getTable()+" where company_id=?";
        if(isOwner != null){
            query+= " AND is_owner = "+((isOwner)?"1":"0");
        }
        PreparedStatement stmt = _conn.prepareStatement(query);
        stmt.setInt(1,companyId);
        ResultSet rs = stmt.executeQuery();
        while( rs.next()){
            list.add(resultToJson(rs));
        }
        return list;
    }
}