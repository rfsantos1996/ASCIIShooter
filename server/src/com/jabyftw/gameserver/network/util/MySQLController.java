package com.jabyftw.gameserver.network.util;

import java.sql.SQLException;

/**
 * Created by Rafael on 07/02/2015.
 */
public abstract class MySQLController {

    protected final MySQLConnection mySQLConnection;

    protected MySQLController(MySQLConnection mySQLConnection) {
        this.mySQLConnection = mySQLConnection;
    }

    public abstract void updateDatabase(int actualRevision) throws SQLException;

    public abstract void initialize() throws SQLException;

    public abstract void dispose() throws SQLException;

}
