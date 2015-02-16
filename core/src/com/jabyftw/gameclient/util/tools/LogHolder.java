package com.jabyftw.gameclient.util.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.util.GameDrawable;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;

import java.util.Iterator;

/**
 * Created by Rafael on 05/02/2015.
 */
public class LogHolder implements Tickable, GameDrawable {

    private final Array<LogMessage> logMessages = new Array<LogMessage>();
    private final FontEnum fontEnum;
    private final Color fontColor;
    private final int maxLines;
    private final Vector2 location = new Vector2();

    private LogHolder(FontEnum fontEnum, int maxLines, Color fontColor) {
        this.fontEnum = fontEnum;
        this.maxLines = maxLines;
        this.fontColor = fontColor;
    }

    @Override
    public void drawGame(SpriteBatch batch) {
        for(int line = 0; line < logMessages.size; line++) {
            if(line >= maxLines) return;

            LogMessage logMessage = logMessages.get(line);
            BitmapFont bitmapFont = Resources.getBitmapFont(fontEnum);

            Util.drawText(bitmapFont, batch, logMessage.getMessage(), fontColor, location.x, location.y + (2 * bitmapFont.getLineHeight() / 3f) + (line * bitmapFont.getLineHeight() * 1.5f));
        }
    }

    @Override
    public void update(float deltaTime) {
        Iterator<LogMessage> iterator = logMessages.iterator();
        int size = logMessages.size;

        while(iterator.hasNext() && size >= 0) {
            LogMessage next = iterator.next();
            {
                next.update(deltaTime);
                if(!next.isAlive())
                    iterator.remove();
            }
            size--;
        }
    }

    LogMessage createMessage(String message, float duration) {
        System.out.println(message);
        LogMessage logMessage;
        logMessages.insert(0, (logMessage = new LogMessage(message, duration)));
        return logMessage;
    }
}
