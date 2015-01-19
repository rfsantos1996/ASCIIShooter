package com.jabyftw.gameserver.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Rafael on 12/01/2015.
 */
public class MySQLConnection {

    public static final String BASE_MYSQL_URL = "jdbc:mysql://%host%:%port%/%database%";

    private final String username, password, url;
    private Connection connection;

    public MySQLConnection(String username, String password, String host, int port, String database) {
        this.username = username;
        this.password = password;
        this.url = BASE_MYSQL_URL.replaceAll("%host%", host).replaceAll("%port%", String.valueOf(port)).replaceAll("%database%", database);
    }

    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()) {
                closeConnection();
                connection = DriverManager.getConnection(url, username, password);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
