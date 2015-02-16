package com.jabyftw.gameclient.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.util.HudDrawable;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;

/**
 * Created by Rafael on 07/12/2014.
 */
public class ButtonTable implements HudDrawable, Tickable, Disposable {

    private final Array<Button> buttons = new Array<Button>();
    private final BitmapFont font;
    private final Color baseColor, selectedColor;
    private float offsetX, offsetY;
    private int buttonIndex = 0;

    public ButtonTable(FontEnum font, Color baseColor, Color selectedColor) {
        this(font, baseColor, selectedColor, 0, 0);
    }

    private ButtonTable(FontEnum font, Color baseColor, Color selectedColor, float offsetX, float offsetY) {
        this.font = Resources.getBitmapFont(font);
        this.baseColor = baseColor;
        this.selectedColor = selectedColor;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void update(float deltaTime) {
        for(int i = 0; i < buttons.size; i++) {
            buttons.get(i).update(deltaTime, i == buttonIndex);
        }
    }

    @Override
    public void drawHUD(SpriteBatch batch) {
        for(int i = 0; i < buttons.size; i++) {
            buttons.get(i).setColor(buttonIndex == i ? selectedColor : baseColor);
            buttons.get(i).setLocation(offsetX, getYForButton(i) + offsetY);
            buttons.get(i).drawHUD(batch);
        }
    }

    @Override
    public void dispose() {
        for(Button button : buttons) {
            button.dispose();
        }
    }

    /*
     * This will return the Y for the button (this will work as long as the font isn't too large or having a lot of buttons)
     */
    private float getYForButton(int index) {
        float fullSize = buttons.size, halfSize = fullSize / 2f;
        return -(index - ((fullSize % 2 == 0) ? halfSize : ((fullSize - 1) / 2f))) * (font.getLineHeight() * 2f);
    }

    public Array<Button> getButtons() {
        return buttons;
    }

    public void addButton(Button button) {
        buttons.add(button);
        if(button.getFont() == null)
            button.setFont(font);
    }

    Button getSelectedButton() {
        return buttons.get(buttonIndex);
    }

    public int getButtonIndex() {
        return buttonIndex;
    }

    public void setButtonIndex(int buttonIndex) {
        this.buttonIndex = buttonIndex;
        checkButtonIndex();
    }

    void addButtonIndex() {
        this.buttonIndex++;
        checkButtonIndex();
    }

    void subtractButtonIndex() {
        this.buttonIndex--;
        checkButtonIndex();
    }

    private void checkButtonIndex() {
        if(this.buttonIndex < 0)
            this.buttonIndex = buttons.size - 1;
        if(this.buttonIndex >= buttons.size)
            this.buttonIndex = 0;
    }

    public void setOffsets(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public static class DesktopInputAdapter extends InputAdapter {

        public static DesktopInputAdapter getInputAdapter(ButtonTable buttonTable, boolean includeEscape) {
            return new DesktopInputAdapter(buttonTable, includeEscape);
        }

        private final ButtonTable buttonTable;
        private final boolean includeEscape;

        public DesktopInputAdapter(ButtonTable buttonTable, boolean includeEscape) {
            this.buttonTable = buttonTable;
            this.includeEscape = includeEscape;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            {
                Vector3 unproject = Main.getInstance().getHudViewport().unproject(new Vector3(screenX, screenY, 0));
                screenX = (int) unproject.x;
                screenY = (int) unproject.y;
            }
            for(int i = 0; i < buttonTable.buttons.size; i++) {
                if(buttonTable.buttons.get(i).isMouseInside(screenX, screenY)) {
                    buttonTable.buttonIndex = i;
                    return true;
                }
            }
            return super.mouseMoved(screenX, screenY);
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            {
                Vector3 unproject = Main.getInstance().getHudViewport().unproject(new Vector3(screenX, screenY, 0));
                screenX = (int) unproject.x;
                screenY = (int) unproject.y;
            }
            for(int i = 0; i < buttonTable.buttons.size; i++) {
                Button buttonClicked = buttonTable.buttons.get(i);
                if(buttonClicked.isMouseInside(screenX, screenY)) {
                    buttonTable.buttonIndex = i;
                    buttonClicked.doButtonAction(Input.Buttons.LEFT == button, buttonClicked.getRepeatedlyTimesPressed());
                    buttonClicked.doTimesPressed(true);
                    return true;
                } else {
                    buttonClicked.doTimesPressed(false);
                }
            }
            return super.touchDown(screenX, screenY, pointer, button);
        }

        @Override
        public boolean keyDown(int keycode) {
            if((keycode == Input.Keys.S || keycode == Input.Keys.DOWN) && !(buttonTable.getSelectedButton() instanceof TextInputButton)) {
                buttonTable.addButtonIndex();
                return true;
            }
            if((keycode == Input.Keys.W || keycode == Input.Keys.UP) && !(buttonTable.getSelectedButton() instanceof TextInputButton)) {
                buttonTable.subtractButtonIndex();
                return true;
            }
            if(keycode == Input.Keys.ENTER || keycode == Input.Keys.CENTER ||
                    (buttonTable.buttons.get(buttonTable.buttonIndex).isAcceptingSideKeys() && (keycode == Input.Keys.A || keycode == Input.Keys.LEFT || keycode == Input.Keys.D || keycode == Input.Keys.RIGHT))) {
                for(int i = 0; i < buttonTable.buttons.size; i++) {
                    Button currentIndexButton = buttonTable.buttons.get(i);
                    if(i == buttonTable.buttonIndex) {
                        if(currentIndexButton instanceof TextInputButton) {
                            buttonTable.addButtonIndex();
                            return true;
                        } else {
                            currentIndexButton.doButtonAction(keycode != Input.Keys.A && keycode != Input.Keys.LEFT, currentIndexButton.getRepeatedlyTimesPressed());
                            currentIndexButton.doTimesPressed(true);
                        }
                    } else {
                        currentIndexButton.doTimesPressed(false);
                    }
                }
                return true;
            }
            if(keycode == Input.Keys.ESCAPE && includeEscape) {
                Main.setCurrentGameState(null);
                return true;
            }
            return super.keyDown(keycode);
        }

        @Override
        public boolean keyTyped(char character) {
            Button selectedButton = buttonTable.buttons.get(buttonTable.buttonIndex);
            if(selectedButton instanceof TextInputButton) {
                ((TextInputButton) selectedButton).keyTyped(character);
                return true;
            }
            return super.keyTyped(character);
        }
    }
}
