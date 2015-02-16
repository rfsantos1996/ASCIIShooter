package com.jabyftw.gameclient.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.jabyftw.gameclient.util.Constants;

import java.util.regex.Pattern;

/**
 * Created by Rafael on 07/01/2015.
 */
public abstract class TextInputButton extends Button {

    protected String input = "";
    protected String pattern = Constants.Util.LETTERS_AND_NUMBERS;
    protected boolean hideInput = false;

    public TextInputButton(String text) {
        super(text, false);
        setInput();
    }

    public TextInputButton(BitmapFont font, String text) {
        super(font, text, false);
        setInput();
    }

    public TextInputButton(BitmapFont font, String text, Color color) {
        super(font, text, false, color);
        setInput();
    }

    public TextInputButton(BitmapFont font, String text, Color color, float x, float y) {
        super(font, text, false, color, x, y);
        setInput();
    }

    @Override
    public void update(float deltaTime, boolean isSelected) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && input.length() > 0 && isSelected)
            input = input.substring(0, input.length() - 1);
        setDisplayText(getText().replaceAll("%input%", hideInput ? getHiddenInput(input.length()) : input));
    }

    private String getHiddenInput(int length) {
        String text = "";
        for(int i = 0; i < length; i++)
            text += "*";
        return text;
    }

    @Override
    public void doButtonAction(boolean positiveAction, int timesPressed) {
    }

    public void keyTyped(char character) {
        if(Pattern.matches(pattern, "" + character))
            input += character;
    }

    public String getInput() {
        return input;
    }

    public abstract void setInput();
}
