package com.jabyftw.gameclient.entity.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.maps.Converter;
import com.jabyftw.gameclient.util.StringReplacer;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;

/**
 * Created by Isa on 04/01/2015.
 */
public class DisplayText implements Tickable, Disposable {

    private final Entity holder;
    private final BitmapFont font;
    private final float duration;

    private String text;
    private StringReplacer stringReplacer;
    private float elapsedDuration = 0;

    public DisplayText(Entity holder, FontEnum font, String text, StringReplacer stringReplacer, float duration) {
        this.holder = holder;
        this.font = Resources.getBitmapFont(font);
        this.text = text;
        this.stringReplacer = stringReplacer;
        this.duration = duration;
        this.holder.addDisplayTextAtHead(this);
    }

    public DisplayText(Entity holder, FontEnum font, String text, float duration) {
        this(holder, font, text, null, duration);
    }

    public DisplayText(Entity holder, String text, StringReplacer stringReplacer, float duration) {
        this(holder, FontEnum.PRESS_START_10, text, stringReplacer, duration);
    }

    public DisplayText(Entity holder, FontEnum font, String renderingText) {
        this(holder, font, renderingText, Main.STEP * 2f);
    }

    public DisplayText(Entity holder, String renderingText) {
        this(holder, renderingText, null, Main.STEP * 2f);
    }

    @Override
    public void update(float deltaTime) {
        elapsedDuration += deltaTime;
        if(elapsedDuration >= duration)
            dispose();
    }

    public void draw(SpriteBatch batch, int index) {
        Vector2 location = Converter.BOX2D_COORDINATES.toScreenCoordinates(holder.getLocation().cpy()).add(Converter.WORLD_COORDINATES.toScreenCoordinates(new Vector2(0, index + 1 + 0.5f)));

        String displayText = stringReplacer != null ? stringReplacer.replace(text) : text;
        Util.drawText(
                font,
                batch,
                displayText,
                Color.ORANGE,
                location.x - ((displayText.length() / 2f) * font.getSpaceWidth()),
                location.y
        );
    }

    public String getText() {
        return text;
    }

    @Override
    public void dispose() {
        holder.removeDisplayText(this);
    }
}
