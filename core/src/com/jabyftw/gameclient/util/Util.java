package com.jabyftw.gameclient.util;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.event.Listener;
import com.jabyftw.gameclient.event.util.ListenerFilter;
import com.jabyftw.gameclient.maps.Converter;
import com.jabyftw.gameclient.screen.PlayerLight;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * Created by Rafael on 05/12/2014.
 */
public abstract class Util {

    public static int TEXT_SHADOW_OFFSET_SIZE = 2;

    public static void drawText(BitmapFont font, SpriteBatch batch, String text, float x, float y) {
        drawText(font, batch, text, null, x, y);
    }

    public static void drawText(BitmapFont font, SpriteBatch batch, String text, Color color, float x, float y) {
        drawText(font, batch, text, color, x, y, TEXT_SHADOW_OFFSET_SIZE, TEXT_SHADOW_OFFSET_SIZE);
    }

    public static void drawText(BitmapFont font, SpriteBatch batch, String text, Color color, float x, float y, int shadowOffsetX, int shadowOffsetY) {
        Color original = font.getColor();
        {
            batch.begin();
            // Draw shadow first
            if(TEXT_SHADOW_OFFSET_SIZE > 0) {
                font.setColor(Constants.Util.TEXT_SHADOW_COLOR.cpy());
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

    /*public static long getUTCTime() {
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
    }*/

    private static PlayerLight createPointLight(RayHandler rayHandler, int rays, Color color, float lightDistance) {
        PlayerLight playerLight = new PlayerLight(rayHandler, rays, color, Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(lightDistance * 1.4f, 0)).x, 0, 0);
        playerLight.setSoft(true);
        playerLight.setSoftnessLength(0.5f);
        return playerLight;
    }

    public static PlayerLight createPointLight(RayHandler rayHandler, int rays, Color color, float lightDistance, Body box2dBody) {
        PlayerLight playerLight = createPointLight(rayHandler, rays, color, lightDistance);
        playerLight.attachToBody(box2dBody);
        return playerLight;
    }

    public static void handleAnnotationEventSystem(ListenerFilter filter, Array<Listener> listenerArray) {
        Class<? extends Annotation> annotationClass = filter.getAnnotationClass();

        for(Listener listener : new Array.ArrayIterator<Listener>(listenerArray)) {
            Class<? extends Listener> listenerClass = listener.getClass();

            for(Method method : listenerClass.getDeclaredMethods()) {
                if(method.isAnnotationPresent(annotationClass) && method.getParameterCount() == 1) {
                    Annotation annotation = method.getAnnotation(annotationClass);

                    if(filter.filterAnnotation(method, annotation)) {
                        try {
                            method.invoke(listener, filter.getMethodArguments());
                        } catch(Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    public static int fixIndex(int index, int length) {
        if(index >= length)
            index = 0;
        else if(index < 0)
            index = length - 1;

        return index;
    }
}
