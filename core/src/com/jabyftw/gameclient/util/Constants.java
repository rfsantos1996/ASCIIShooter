package com.jabyftw.gameclient.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.jabyftw.gameclient.maps.Converter;

import java.util.concurrent.TimeUnit;

/**
 * Created by Rafael on 04/02/2015.
 */
@SuppressWarnings("ConstantConditions")
public abstract class Constants {

    public static final boolean isTestBuild = true;
    public static final boolean isEncryptingPackets = true;
    public static boolean isDebugging = false;
    public static boolean isDebuggingNetwork = false;

    public static final String TEST_VERSION_NAME = isTestBuild ? " - TEST VERSION" : "";
    public static final String GAME_NAME_CLIENT = "<GAME NAME HERE>";
    public static final String GAME_NAME_SERVER = "<GAME NAME HERE - SERVER" + TEST_VERSION_NAME + ">";

    public static final float GAME_VERSION = 0.1f;
    public static final float ONLINE_PROFILE_VERSION = 0.1f;
    public static final float OFFLINE_PROFILE_VERSION = 0.1f;
    public static final float SERVER_PROPERTIES_VERSION = 0.1f;
    public static final float LANGUAGE_VERSION = -1;
    public static final int MYSQL_REVISION = 1;

    /**
     * Created by Rafael on 04/02/2015.
     */
    public static class Display {

        public static final String WINDOW_TITLE = Constants.GAME_NAME_CLIENT + " v" + GAME_VERSION;
        public static final int V_WIDTH = 740;
        public static final int V_HEIGHT = 480;
        public static final float PIXELS_PER_METER = 100f;

        public static final int TILE_WIDTH = 8;
        public static final int TILE_HEIGHT = 8;
        public static final float BASE_TILE_SCALE = 2.0f;

        public static final int DEFAULT_TEXT_SHADOW_OFFSET = 2;
        public static final float WAIT_TIME_AFTER_RESPONSE = 1.2f;
    }

    /**
     * Created by Rafael on 04/02/2015.
     */
    public static class Box2dConstants {

        // Category bits
        public static final short BIT_EMPTY = 0;

        public static final short BIT_SOLID_BLOCK = 2; // << 0;
        public static final short BIT_PLAYER_ENTITY = 2 << 1;
        public static final short BIT_ENEMY_ENTITY = 2 << 2;
        public static final short BIT_BULLET_ENTITY = 2 << 3;
        public static final short BIT_LIGHT = 2 << 4;
        public static final short BIT_WORLD_BOUNDS = 2 << 5;
    }

    /**
     * Created by Rafael on 04/02/2015.
     */
    public static class Gameplay {

        // Time
        public static final float STEP = 1f / 60f;
        public static final float PACKET_HANDLER_STEP = STEP / 2f;
        public static final float SECONDS_TO_GARBAGE_COLLECTOR = 15;
        // Text length
        public static final int TEXT_TOO_SHORT = 3;
        public static final int TEXT_TOO_LONG = 16;

        /**
         * Created by Rafael on 13/02/2015.
         */
        public static class Entities {

            public static final Vector2 BODY_RADIUS = Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(1 / 2f / 2f, 1 / 2f / 2f).scl(MathUtils.cosDeg(45), MathUtils.sinDeg(45)));

            public static final float DEFAULT_HEALTH = 10f;

            public static final float DEFAULT_INVINCIBILITY_TIME_AFTER_DAMAGE = 0.125f;
            public static final float DEFAULT_INVINCIBILITY_TIME_AFTER_SPAWN = 2f;
        }

        /**
         * Created by Rafael on 14/02/2015.
         */
        public static class Target {

            public static final float TIME_UNTIL_DAMAGE_CLEARS = 2.5f;
        }

        /**
         * Created by Rafael on 13/02/2015.
         */
        public static class Bullet {

            public static final Color BULLET_COLOR = Color.RED;
            public static final int BULLET_LIGHT_QUALITY = 56;
        }

        /**
         * Created by Rafael on 04/02/2015.
         */
        public static class Player {

            public static int MAXIMUM_PROFILE_LEVEL = -1;
            public static final int DEFAULT_PROFILE_LEVEL = 1;
            public static final int NUMBER_OF_LAYOUTS = 5;
            public static final Color PLAYER_LIGHT_COLOR = new Color(1, 1, 1, 0.75f);

            // Distance
            public static final float INTERACT_DISTANCE = 2.5f;
            public static final float VIEW_DISTANCE = 12;
            // Speed
            public static final float BASE_SPEED = 2.3f;
            public static final float RUNNING_SPEED = 1.4f;
            public static final float MAX_ROTATION_SPEED = 360 * STEP;
            // Time
            public static final float MAXIMUM_STAMINA = 3.6f;
            public static final float STAMINA_RECOVER_COOLDOWN = 3.2f;
            public static final float WEAPON_CHANGE_TIME = 0.7f;
        }
    }

    /**
     * Created by Rafael on 04/02/2015.
     */
    public static class Multiplayer {

        public static final String CONNECTION_HOST = "localhost";
        public static final String BASE_MYSQL_URL = "jdbc:mysql://%host%:%port%/%database%";
        public static final int NUMBER_OF_SERVER_THREADS = 24;
        public static final int CONNECTION_PORT = 5450;

        public static final float MAXIMUM_PING_ALLOWED = 1000f; // In milliseconds
        public static final float SERVER_ALLOWANCE = 1.15f;
        public static final float CLIENT_TIMEOUT_TIME = 3f;
        public static final float SERVER_TIMEOUT_TIME = 7.5f;
        public static final float TIME_FOR_CLIENT_KEEP_AWAKE_PACKET = (SERVER_TIMEOUT_TIME * 0.75f) - ((MAXIMUM_PING_ALLOWED / (float) TimeUnit.SECONDS.toMillis(1)) * SERVER_ALLOWANCE);
    }

    /**
     * Created by Rafael on 04/02/2015.
     */
    public static class Util {

        // Patterns
        public static final String LETTERS_AND_NUMBERS = "[a-zA-Z0-9]";
        public static final String LETTERS_AND_NUMBERS_WITH_SPACE = "[a-zA-Z0-9 ]";

        public static final String LANG_VERSION_STRING = "LangVersion";

        public static final Color DEBUG_COLOR = new Color(1, 1, 1, 0.6f);
        public static final Color TEXT_SHADOW_COLOR = new Color(0, 0, 0, 0.57f);
    }
}
