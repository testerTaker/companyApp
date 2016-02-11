/**
* Created by Fabio Oliveira Costa 
* 
*  Base for all model and db interaction
*/
package br.com.dexus.models;

import java.sql.Connection;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public abstract class AbstractModel {



    protected Connection _conn;
    protected HashMap<String,String> _validationErrors = new HashMap<String,String>();
    protected HashMap<String,DataBaseField> _fields = new HashMap<String,DataBaseField>();
    protected HashMap<String,Object>  _values = new HashMap<String,Object>();

    /**
    * Used on list fetches
    * @param conn databse connection 
    */
    public  AbstractModel(Connection conn){
        _conn = conn;
        _fields = _getFields();
    }

    /**
    * Used on single item fetch
    * @param conn databse connection 
    * @param id PK of the object
    */
    public  AbstractModel(Connection conn,int id)  throws SQLException{
        this(conn);
        this._getFromDb(id);
    }

    /**
    * Used on item creation
    * @param conn databse connection 
    * @param json object data comming from request
    */
    public  AbstractModel(Connection conn, JSONObject json){
        this(conn);
        _fromJSON(json);
    }

    /**
    * Used on item edition
    * @param conn databse connection 
    * @param id PK of the item
    * @param json object data comming from request
    */
    public  AbstractModel(Connection conn, int id,JSONObject json) throws SQLException{
        this(conn, id);
        _fromJSON(json,true);
    }

    /**
    * Save the data on db if it's valid
    * @return True on success, false otherwise
    */
    public boolean save() throws SQLException {
        if(!isValid()){
            return false;
        }
        return _saveOnDb();
    }

    /**
    * Get errors that are setup validation
    */
    public HashMap<String,String> getErrors(){
        return _validationErrors;
    }

    /**
    * Validate the required fields if any
    */
    protected boolean _validateRequired(){

        Object value;
        Method method;
        boolean isValid = true;
        boolean fieldInvalid;
        String fieldName;
        for (DataBaseField field : _fields.values()) {
            fieldInvalid = false;

            if(!field.isRequired()){
                continue;
            }
            fieldName = field.getName();
            value = _values.get(fieldName);
            if(value==null){
                fieldInvalid = true;
            }
            else if(field.getType()=="string"){
                if(((String) value).trim().isEmpty()){
                    fieldInvalid = true;
                }
            }
             if(fieldInvalid){
                isValid =  false;
                _validationErrors.put(fieldName,fieldName+" is required and was missing");
            }
        }
      
        return isValid;
    }

    /**
    * Checks if the data is valid
    */
    public  boolean isValid(){
        this._validationErrors = new HashMap<String,String>();
        return this._validateRequired();
    }
    /**
    * Populate the object with json data
    */
    protected void _fromJSON(JSONObject json){
        _fromJSON(json,false);
    }        

    /**
    * Get data from the db and populate the object
    * @param id pk of the object
    */
    protected void _getFromDb(Integer id) throws SQLException {
        PreparedStatement stmt = _conn.prepareStatement("select * from "+_getTable()+" WHERE id=?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if(!rs.next()){
            throw new RuntimeException("Record not found");
        }
        _setValues(rs);
    }

    /**
    * List all items of the data base
    */
    public JSONArray listAll() throws SQLException{
        JSONArray list = new JSONArray();

        Statement stmt = _conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from "+_getTable());
        while( rs.next()){
            list.add(resultToJson(rs));
        }
        return list;
    }

    /**
    * Populate the object with a result of a query
    * @param rs a query with this field data
    */
    protected  void _setValues(ResultSet rs) throws SQLException{
        String fieldName;
        for (DataBaseField field : _fields.values()) {
            fieldName = field.getName();
            if(field.getType() =="int"){
                _values.put(fieldName,(Integer) rs.getInt(fieldName));
            }
            else{
              _values.put(fieldName,rs.getString(fieldName));

            }
        }
    }

    /**
    * Populate the object with json data
    * If isUpdate will only populate the incoming fields
    * @param json Json data from a request
    * @param isUpdate whether this is an update
    */
    protected void _fromJSON(JSONObject json,boolean isUpdate){
        String fieldName;
        for (DataBaseField field : _fields.values()) {
            fieldName = field.getName();
            Object defaulValue = isUpdate ? _values.get(fieldName):null;
            _values.put(fieldName, json.getOrDefault(fieldName,defaulValue));
        }
    }
    /**
    * Serialize the object on a JSONObvject
    */
    public  JSONObject toJson(){
       JSONObject obj=new JSONObject();
        String fieldName;
        for (DataBaseField field : _fields.values()) {
            fieldName = field.getName();
            obj.put(fieldName,_values.get(fieldName));
        }
        return obj;
    }

    /**
    * Serialize a result set into JSON object
    */
    public JSONObject resultToJson(ResultSet rs) throws SQLException{
        String fieldName;
        JSONObject obj=new JSONObject();
        for (DataBaseField field : _fields.values()) {
            fieldName = field.getName();
            if(field.getType() =="int"){
                obj.put(fieldName,rs.getInt(fieldName));
            }
            else{
                obj.put(fieldName,rs.getString(fieldName));
            }
        }
        return obj;
    }
    /**
    * Save this object on data base
    */
    protected  boolean _saveOnDb() throws SQLException{
        StringBuilder query = new StringBuilder("insert into "+_getTable()+" (");
        StringBuilder values = new StringBuilder(") values (");
        String fieldName  = null;
        String prefix = "";
        DataBaseField primary = null;
        for (DataBaseField field : _fields.values()) {
            if(field.isPrimary()){
                primary = field;
                continue;
            }
            query.append(prefix);
            query.append(field.getName());
            values.append(prefix);
            values.append("?");
            prefix = ",";
        }
        query.append(values).append(")");
        PreparedStatement stmt = _conn.prepareStatement(query.toString(),Statement.RETURN_GENERATED_KEYS);
        int index =1;        
        for (DataBaseField field : _fields.values()) {
            if(field.isPrimary()){
                continue;
            }
            fieldName = field.getName();
            if(field.getType() =="int"){
                stmt.setInt(index,Integer.parseInt(_values.get(fieldName).toString()));
            }
            else{
                stmt.setString(index,(String) _values.get(fieldName));
            }
            index ++;
        }
        stmt.execute();
        stmt.close();

        ResultSet rs = stmt.getGeneratedKeys();
        if(rs.next())
        {
            int lastInsetedId = rs.getInt(1);
            _values.put(primary.getName(),lastInsetedId);
        }
        return true;
    }
    /**
    * Update this object on database
    */
    public boolean update() throws SQLException{
        StringBuilder query = new StringBuilder("UPDATE "+_getTable()+" SET ");
        String fieldName = null;
        String prefix = "";
        DataBaseField primary = null;
        for (DataBaseField field : _fields.values()) {
            if(field.isPrimary()){
                primary = field;
                continue;
            }   
        fieldName = field.getName();

            query.append(prefix).append(fieldName).append("=? ");
            prefix = ",";
        }
        query.append("WHERE ").append(primary.getName()).append("= ?");
        PreparedStatement stmt = _conn.prepareStatement(query.toString());
        int index =1;        
        for (DataBaseField field : _fields.values()) {
            if(field.isPrimary()){
                continue;
            }
            fieldName = field.getName();
            if(field.getType() =="int"){
                stmt.setInt(index,Integer.parseInt(_values.get(fieldName).toString()));
            }
            else{
                stmt.setString(index,(String) _values.get(fieldName));
            }
            index ++;
        }
        int id = Integer.parseInt(_values.get(primary.getName()).toString());
        stmt.setInt(index, id);
        stmt.execute();
        stmt.close();
        return true;
    }

    /**
    * Gets the dschema of the table
    */
    protected abstract HashMap<String,DataBaseField>  _getFields();

    /**
    * Gets the name of the table
    */
    protected abstract String _getTable();
}