package com.jabyftw.gameclient.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.util.Drawable;
import com.jabyftw.gameclient.util.Util;

/**
 * Created by Rafael on 07/12/2014.
 */
public abstract class Button implements Drawable, Disposable {

    private final String text;
    private final boolean acceptsSideKeys;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private String displayText;
    private BitmapFont font = null;
    private Color color;
    private float x = 0, y = 0;

    private long tickPressed = 0;
    private int timesPressed = 0;

    public Button(String text, boolean acceptsSideKeys) {
        this.text = text;
        this.displayText = text;
        this.acceptsSideKeys = acceptsSideKeys;
    }

    public Button(BitmapFont font, String text, boolean acceptsSideKeys) {
        this(text, acceptsSideKeys);
        this.font = font;
    }

    public Button(BitmapFont font, String text, boolean acceptsSideKeys, Color color) {
        this(font, text, acceptsSideKeys);
        this.color = color;
    }

    public Button(BitmapFont font, String text, boolean acceptsSideKeys, Color color, float x, float y) {
        this(font, text, acceptsSideKeys, color);
        this.x = x;
        this.y = y;
    }

    public abstract void update(float deltaTime, boolean isSelected);

    @Override
    public void draw(SpriteBatch batch) {
        float displayX = x - (font.getSpaceWidth() * (displayText.length() / 2f)),
                displayY = y;
        Util.drawText(font, batch, displayText, color, displayX, displayY);

        if(Main.isDebugging) {
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(1, 1, 0, 1);
                shapeRenderer.rect(displayX, displayY - (font.getLineHeight() * 0.3f), font.getSpaceWidth() * text.length(), font.getLineHeight());
                shapeRenderer.end();
            }
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    public boolean isAcceptingSideKeys() {
        return acceptsSideKeys;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isMouseInside(float scaledX, float scaledY) {
        float minX = x - (font.getSpaceWidth() * (displayText.length() / 2f)),
                maxX = (minX + ((float) displayText.length() * font.getSpaceWidth()));
        float minY = y - (font.getLineHeight() * 0.5f),
                maxY = y + (font.getLineHeight() * 0.5f);
        return scaledX >= minX && scaledX <= maxX
                && scaledY >= minY && scaledY <= maxY;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract void doButtonAction(boolean positiveAction, int timesPressed);

    public int getRepeatedlyTimesPressed() {
        if(Main.getTicksPassed() - tickPressed >= (0.4f / Main.STEP))
            doTimesPressed(false);
        return timesPressed;
    }

    public void doTimesPressed(boolean pressed) {
        if(pressed) {
            timesPressed++;
            tickPressed = Main.getTicksPassed();
        } else {
            timesPressed = 0;
        }
    }
}
