package com.jabyftw.gameclient.util;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.jabyftw.gameclient.maps.Map;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Rafael on 05/12/2014.
 */
public abstract class Util {

    public static final String LETTERS_AND_NUMBERS = "[a-zA-Z0-9]";

    public static final Color DEBUG_COLOR = new Color(1, 1, 1, 0.6f);
    public static final float TEXT_SHADOW_OPACITY = 0.57f;
    public static int TEXT_SHADOW_OFFSET_SIZE = 2;

    public static void drawText(BitmapFont font, SpriteBatch batch, String text, float x, float y) {
        drawText(font, batch, text, null, x, y);
    }

    public static void drawText(BitmapFont font, SpriteBatch batch, String text, Color color, float x, float y) {
        drawText(font, batch, text, color, x, y, TEXT_SHADOW_OFFSET_SIZE, TEXT_SHADOW_OFFSET_SIZE);
    }

    public static void drawText(BitmapFont font, SpriteBatch batch, String text, Color color, float x, float y, float shadowOffsetX, float shadowOffsetY) {
        Color original = font.getColor();
        {
            batch.begin();
            // Draw shadow first
            if(TEXT_SHADOW_OFFSET_SIZE > 0) {
                font.setColor(Color.BLACK.cpy().add(0, 0, 0, -TEXT_SHADOW_OPACITY));
                font.drawMultiLine(batch, text, x + shadowOffsetX, y + (font.getLineHeight() / 2f) + (-shadowOffsetY));
            }
            // Draw text
            {
                font.setColor(color != null ? color : Color.WHITE);
                font.drawMultiLine(batch, text, x, y + (font.getLineHeight() / 2f));
            }
            batch.end();
        }
        font.setColor(original);
    }

    public static String formatDecimal(double value, int numberOfCharactersAfterComma) {
        boolean isFirst = true;
        String formatPattern = "0";
        while(numberOfCharactersAfterComma > 0) {
            formatPattern += isFirst ? ".0" : "#";
            isFirst = false;
            numberOfCharactersAfterComma--;
        }
        return new DecimalFormat(formatPattern).format(value).replaceFirst(",", ".");
    }

    public static float square(float value) {
        return value * value;
    }

    public static int getButtonTimesPressedValue(boolean positiveAction, int timesPressed) {
        int value = 1;
        while((timesPressed -= 5) > 0)
            value *= 2;
        return value * (positiveAction ? 1 : -1);
    }

    public static long getUTCTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String formattedString = dateFormat.format(new Date());

        System.out.println(formattedString);
        try {
            return dateFormat.parse(formattedString).getTime();
        } catch(ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static PointLight createPointLight(RayHandler rayHandler, int rays, Color color, float lightDistance) {
        PointLight pointLight = new PointLight(rayHandler, rays, color, lightDistance * 1.4f * Map.BOX2D_TILE_SCALE_WIDTH, 0, 0);
        pointLight.setSoft(true);
        pointLight.setSoftnessLength(0.5f);
        return pointLight;
    }

    public static PointLight createPointLight(RayHandler rayHandler, int rays, Color color, float lightDistance, Body box2dBody) {
        PointLight pointLight = createPointLight(rayHandler, rays, color, lightDistance);
        pointLight.attachToBody(box2dBody);
        return pointLight;
    }
}
