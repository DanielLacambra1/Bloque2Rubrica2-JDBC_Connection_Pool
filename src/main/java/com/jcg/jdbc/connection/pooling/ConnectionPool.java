package com.jcg.jdbc.connection.pooling;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

public class ConnectionPool {
	// Nombre y URL de bbdd de JDBC Driver 
	
	// com.mysql.jdbc.Driver está extinguido, hay que utilizar com.mysql.cj.jdbc.Driver
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    static final String JDBC_DB_URL = "jdbc:mysql://localhost:3306/tutorialDb";
 
    // Credenciales de la bbdd JDBC
    static final String JDBC_USER = "root";
    static final String JDBC_PASS = "mysqlrootpasS1-";
 
    private static GenericObjectPool gPool = null;
 
    @SuppressWarnings("unused")
    public DataSource setUpPool() throws Exception {
        Class.forName(JDBC_DRIVER);
 
        // Creamos una instancia de GenericObjectPool que contiene nuestro objeto Pool de conexiones
        gPool = new GenericObjectPool();
        gPool.setMaxActive(5);
 
        // Creamos objeto ConnectionFactory que será utilizado por Pool para crear el objeto de conexión
        ConnectionFactory cf = new DriverManagerConnectionFactory(JDBC_DB_URL, JDBC_USER, JDBC_PASS);
 
        // Creates a PoolableConnectionFactory That Will Wraps the Connection Object Created by the ConnectionFactory to Add Object Pooling Functionality!
        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, null, null, false, true);
        return new PoolingDataSource(gPool);
    }
 
    public GenericObjectPool getConnectionPool() {
        return gPool;
    }
 
    // Imprimimos el status del Pool de conexiones
    private void printDbStatus() {
        System.out.println("Maximum: " + getConnectionPool().getMaxActive() + 
        		"; Active: " + getConnectionPool().getNumActive() + 
        		"; Num Idle: " + getConnectionPool().getNumIdle());
    }
 
    public static void main(String[] args) {
        ResultSet rsObj = null;
        Connection connObj = null;
        PreparedStatement pstmtObj = null;
        ConnectionPool jdbcObj = new ConnectionPool();
        try {   
            DataSource dataSource = jdbcObj.setUpPool();
            jdbcObj.printDbStatus();
 
            System.out.println("\n Creación de un nuevo objeto de conexión para la transacción de la bbdd \n");
            connObj = dataSource.getConnection();
            jdbcObj.printDbStatus(); 
 
            pstmtObj = connObj.prepareStatement("SELECT * FROM technical_editors");
            rsObj = pstmtObj.executeQuery();
            while (rsObj.next()) {
                System.out.println("Nombre de usuario: " + rsObj.getString("tech_username"));
            }
            System.out.println("\n Liberación del objeto de conexión al Pool \n");            
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                if(rsObj != null) {
                    rsObj.close();
                }
                if(pstmtObj != null) {
                    pstmtObj.close();
                }
                if(connObj != null) {
                    connObj.close();
                }
            } catch(Exception sqlException) {
                sqlException.printStackTrace();
            }
        }
        jdbcObj.printDbStatus();
    }
}
