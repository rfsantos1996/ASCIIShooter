package com.jabyftw.gameserver.network.util;

import java.sql.SQLException;

/**
 * Created by Rafael on 07/02/2015.
 */
public class PlayerController extends MySQLController {

    public PlayerController(MySQLConnection mySQLConnection) {
        super(mySQLConnection);
    }

    @Override
    public void initialize() throws SQLException {
        createTable();
    }

    @Override
    public void updateDatabase(int actualRevision) throws SQLException {
    }

    public void createTable() throws SQLException {
        mySQLConnection.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS 'player_storage' (\n" +
                "  'id' INT NOT NULL AUTO_INCREMENT,\n" +
                "  'playerName' VARCHAR(16) NOT NULL,\n" +
                "  'displayName' VARCHAR(16) NOT NULL,\n" +
                "  'level' SMALLINT UNSIGNED NULL DEFAULT 1,\n" +
                "  'exp' FLOAT UNSIGNED NULL DEFAULT 0,\n" +
                "  PRIMARY KEY ('playerName', 'id'),\n" +
                "  UNIQUE INDEX 'playerName_UNIQUE' ('playerName' ASC),\n" +
                "  UNIQUE INDEX 'displayName_UNIQUE' ('displayName' ASC),\n" +
                "  UNIQUE INDEX 'id_UNIQUE' ('id' ASC));\n").execute();
        mySQLConnection.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS 'layouts_storage' (\n" +
                "  'id' INT NOT NULL,\n" +
                "  'layouts' TEXT NOT NULL,\n" +
                "  PRIMARY KEY ('id'),\n" +
                "  UNIQUE INDEX 'id_UNIQUE' ('id' ASC));").execute();
    }

    @Override
    public void dispose() {
    }
}
