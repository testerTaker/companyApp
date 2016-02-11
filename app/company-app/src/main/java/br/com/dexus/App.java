/**
 * Created by Fabio Oliveira Costa 
 * 
 * Main app Entry point
 */
package br.com.dexus;
import static spark.Spark.*;
import java.sql.Connection;
import java.sql.SQLException;
import br.com.dexus.models.*;
import java.lang.reflect.Method;

import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class App {

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
       R apply(T t) throws SQLException;
    }

    public static void main(String[] args) {
        staticFileLocation("/public");
        port(getHerokuAssignedPort());
        setupRoutes( );
    }
     static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    /**
    * Wraps a request call and provides and clkose the connection
    *
    * @param function  the request lambda function
    * @return The output or error string
    */
    public static String callWithConnection(CheckedFunction<Connection, String> function ){
        
        Connection connection  = null;
        try{
            connection = Connector.getConnection();
            return function.apply(connection);
        }
        catch(Exception e) {
          System.err.println(e);
          e.printStackTrace();
          return "{\"success\":false}";
        }
        finally {
          try
          {
            if(connection != null) connection.close();
          }
          catch(SQLException e)
          {
           e.printStackTrace();

            // connection close failed.
            System.err.println(e);
          }
        }
    }
    /**
    * Setup all routes for the app
    */
    public static void setupRoutes( ){
        get("/company", (req, res) -> {
          CheckedFunction<Connection, String> getAllCompanies = conn -> {
                Company company = new Company(conn);
                return company.listAll().toString();
            };
            return callWithConnection(getAllCompanies);
        });
        get("/company/:id", (req, res) -> {
            CheckedFunction<Connection, String> getCompany = conn -> {
                Integer id = Integer.parseInt(req.params(":id"));
                Company company = new Company(conn, id);
                return company.toJson().toString();
            };
            return callWithConnection(getCompany);
        });

        post("/company", (req, res) -> {
            CheckedFunction<Connection, String> saveCompany = conn -> {
                JSONObject json=(JSONObject) JSONValue.parse(req.body());
                Company company = new Company(conn, json);
                if(company.save()){
                    return company.toJson().toString();
                }
                String errorsStr = new JSONObject(company.getErrors()).toString();
                return "{\"success\":false,\"errors\":"+errorsStr+"}";

            };
            return callWithConnection(saveCompany);
        });
        put("/company/:id", (req, res) -> {
            CheckedFunction<Connection, String> updateCompany = conn -> {
                Integer id = Integer.parseInt(req.params(":id"));
                JSONObject json=(JSONObject) JSONValue.parse(req.body());
                Company company = new Company(conn, id, json);
                if(company.update()){
                    return company.toJson().toString();
                }
                String errorsStr = new JSONObject(company.getErrors()).toString();
                return "{\"success\":false,\"errors\":"+errorsStr+"}";
            };
            return callWithConnection(updateCompany);
        });
        post("/company/:id/employee", (req, res) -> {
            CheckedFunction<Connection, String> saveEmployee = conn -> {
                JSONObject json=(JSONObject) JSONValue.parse(req.body());
                Integer id = Integer.parseInt(req.params(":id"));
                json.put("company_id",id);

                Employee employee = new Employee(conn, json);
                if(employee.save()){
                    return employee.toJson().toString();
                }
                String errorsStr = new JSONObject(employee.getErrors()).toString();
                return "{\"success\":false,\"errors\":"+errorsStr+"}";
            };
            return callWithConnection(saveEmployee);
        });
         put("/employee/:id", (req, res) -> {
            CheckedFunction<Connection, String> updateEmployee = conn -> {
                Integer id = Integer.parseInt(req.params(":id"));
                JSONObject json=(JSONObject) JSONValue.parse(req.body());
                Employee employee = new Employee(conn,id, json);
                if(employee.update()){
                    return employee.toJson().toString();
                }
                String errorsStr = new JSONObject(employee.getErrors()).toString();
                return "{\"success\":false,\"errors\":"+errorsStr+"}";
            };
            return callWithConnection(updateEmployee);
        });

       get("/employee/:id", (req, res) -> {
            CheckedFunction<Connection, String> getEmployee = conn -> {
                Integer id = Integer.parseInt(req.params(":id"));
                Employee employee = new Employee(conn, id);
                return employee.toJson().toString();
            };
            return callWithConnection(getEmployee);
        });
        get("/company/:id/employees", (req, res) -> {
           CheckedFunction<Connection, String> getAllEmployees = conn -> {
                Integer id = Integer.parseInt(req.params(":id"));

                Employee employee = new Employee(conn);
                return employee.listAll(id).toString();
            };
            return callWithConnection(getAllEmployees);
        });
       
    }
}