package com.jabyftw.gameserver.network.util;

import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameserver.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Rafael on 12/01/2015.
 */
public class MySQLConnection implements Disposable {

    private final String username, password, url;
    private Connection connection;

    private MySQLController[] controllers = new MySQLController[]{
            new PlayerController(this),
    };

    public MySQLConnection(int revision, String username, String password, String host, int port, String database) throws SQLException {
        this.username = username;
        this.password = password;
        this.url = Constants.Multiplayer.BASE_MYSQL_URL.replaceAll("%host%", host).replaceAll("%port%", String.valueOf(port)).replaceAll("%database%", database);

        for(MySQLController controller : controllers) {
            controller.initialize();
            controller.updateDatabase(revision);
        }
        Server.getInstance().getProperties().setMysqlRevision(Constants.MYSQL_REVISION);
    }

    public Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed()) {
            closeConnection();
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if(connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }

    @Override
    public void dispose() {
        try {
            for(MySQLController controller : controllers) {
                controller.dispose();
            }
            closeConnection();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
