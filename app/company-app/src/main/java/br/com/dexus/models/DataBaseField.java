/**
 * Created by Fabio Oliveira Costa 
 * 
 *  Repesents a database field
 */
package br.com.dexus.models;

public class DataBaseField{
    protected String _name, _type;
    protected boolean _primary;
    protected boolean _required;
    
    public DataBaseField(String name,String type){
        _name = name;
        _type = type;
    }
    public DataBaseField(String name,String type, boolean required){
        this(name,type);
        _required = required;
    }
    public DataBaseField(String name,String type, boolean required, boolean primary){
        this(name,type);
        _required = required;
        _primary = primary;
    }

    public boolean isRequired(){
        return _required;
    }
    public boolean isPrimary(){
        return _primary;
    }
    public String getName(){
        return _name;
    }
    public String getType(){
        return _type;
    }
}