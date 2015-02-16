package com.jabyftw.gameserver.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.util.Constants;

/**
 * Created by Rafael on 05/02/2015.
 */
public class Properties implements Json.Serializable {

    private float propertiesVersion = Constants.SERVER_PROPERTIES_VERSION;
    // Version 1.0
    private String mysqlUsername = "root", mysqlPassword = "123", mysqlHost = "localhost", mysqlDatabase = "asciigame";
    private int mysqlPort = 3306, mysqlRevision = 1;

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public void setMySQLCredentials(String mysqlUsername, String mysqlPassword, String mysqlHost, int mysqlPort, String mysqlDatabase) {
        this.mysqlUsername = mysqlUsername;
        this.mysqlPassword = mysqlPassword;
        this.mysqlHost = mysqlHost;
        this.mysqlPort = mysqlPort;
        this.mysqlDatabase = mysqlDatabase;
    }

    public int getMysqlRevision() {
        return mysqlRevision;
    }

    public void setMysqlRevision(int mysqlRevision) {
        this.mysqlRevision = mysqlRevision;
    }

    @Override
    public void write(Json json) {
        json.writeValue("propertiesVersion", propertiesVersion);
        { // Version 1.0
            // MySQL defaults
            json.writeValue("mysqlUsername", mysqlUsername, String.class);
            json.writeValue("mysqlPassword", mysqlPassword, String.class);
            json.writeValue("mysqlHost", mysqlHost, String.class);
            json.writeValue("mysqlPort", mysqlPort, Integer.class);
            json.writeValue("mysqlDatabase", mysqlDatabase, String.class);
        }
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        propertiesVersion = jsonData.getFloat("propertiesVersion");
        { // Version 1.0
            // MySQL defaults
            setMySQLCredentials(
                    jsonData.getString("mysqlUsername"),
                    jsonData.getString("mysqlPassword"),
                    jsonData.getString("mysqlHost"),
                    jsonData.getInt("mysqlPort"),
                    jsonData.getString("mysqlDatabase")
            );
        }
    }
}
