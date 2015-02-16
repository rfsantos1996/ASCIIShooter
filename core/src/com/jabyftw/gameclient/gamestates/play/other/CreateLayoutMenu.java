package com.jabyftw.gameclient.gamestates.play.other;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.entity.weapon.WeaponSlotType;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;
import com.jabyftw.gameclient.gamestates.util.PseudoGameState;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.network.packets.client.PacketValidateLayoutsRequest;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.screen.TextInputButton;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

import java.util.Iterator;

/**
 * Created by Rafael on 22/01/2015.
 */
public class CreateLayoutMenu extends TabledGameState implements PseudoGameState {

    private final Array<WeaponButton> weaponButtons = new Array<WeaponButton>();
    private final WeaponSlotType[] uniqueTypes = WeaponSlotType.uniqueTypesValues();
    private TextInputButton inputButton;
    private int selectedLayout;

    private boolean changedLayout = false;

    public CreateLayoutMenu() {
        super(true);
    }

    @Override
    public void create() {
        FontEnum font = FontEnum.PRESS_START_20;

        gameStateTitleString = Resources.getLang(LangEnum.CHANGE_LAYOUT_TITLE);
        gameStateTitleFont = FontEnum.PRESS_START_28;

        this.selectedLayout = 0;

        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.SELECT_LAYOUT_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(getText().replaceAll("%layoutname%", getLayouts()[selectedLayout].getDisplayName()));
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    saveLayout(); // Save before changing
                    {
                        selectedLayout += (positiveAction ? 1 : -1);
                        fixSelectedLayout();
                        inputButton.setInput();
                    }
                    for(WeaponButton weaponButton : weaponButtons) {
                        weaponButton.updateForNewLayout();
                    }
                }
            });
            buttonTable.addButton((inputButton = new TextInputButton(Resources.getLang(LangEnum.LAYOUT_DISPLAY_NAME)) {

                @Override
                public void setInput() {
                    pattern = Constants.Util.LETTERS_AND_NUMBERS_WITH_SPACE;
                    input = getLayouts()[selectedLayout].getDisplayName();
                }
            }));
        }
        {
            for(final WeaponSlotType uniqueType : uniqueTypes) {
                WeaponButton weaponButton = new WeaponButton(uniqueType);
                buttonTable.addButton(weaponButton);
                weaponButtons.add(weaponButton);
            }
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.BACK_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    saveLayout();
                    Main.setCurrentGameState(null);
                }
            });
        }
        super.create();
    }

    private void saveLayout() {
        Layout selectedLayout = getLayouts()[this.selectedLayout];

        boolean saved = !inputButton.getInput().equals(selectedLayout.getDisplayName());
        for(WeaponButton weaponButton : weaponButtons) {
            if(weaponButton.saveToLayout())
                saved = true;
        }

        if(saved) {
            selectedLayout.setDisplayName(inputButton.getInput());
            selectedLayout.validate(Main.getOnlineProfile());
            changedLayout = true;
            System.out.println("CreateLayoutMenu.doButtonAction @ CreateLayoutMenu { \"Layout " + (this.selectedLayout + 1) + " saved\" }");
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if(changedLayout) Main.getPacketHandler().addPacketToQueue(new PacketValidateLayoutsRequest(Main.getOnlineProfile().getLayouts()));
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }

    private Layout[] getLayouts() {
        return Main.getOnlineProfile().getLayouts();
    }

    private void fixSelectedLayout() {
        if(selectedLayout >= getLayouts().length)
            selectedLayout = 0;
        else if(selectedLayout < 0)
            selectedLayout = getLayouts().length - 1;
    }

    private class WeaponButton extends Button {

        private static final int ORIGINAL_WEAPON = -1;
        private static final int NULL_WEAPON = -2;

        private final WeaponSlotType uniqueType;

        private final Array<WeaponProperties> validWeapons;
        private WeaponProperties originalWeapon;

        private int selectedWeapon = ORIGINAL_WEAPON;
        private boolean dirty = false;

        public WeaponButton(WeaponSlotType uniqueType) {
            super(Resources.getBitmapFont(FontEnum.PRESS_START_14), Resources.getLang(LangEnum.WEAPON_OPTION_LAYOUT), true);
            this.uniqueType = uniqueType;

            this.validWeapons = WeaponProperties.values(uniqueType.getType());
            updateForNewLayout();

            Iterator<WeaponProperties> iterator = this.validWeapons.iterator();
            int size = validWeapons.size;

            while(iterator.hasNext() && size >= 0) {
                WeaponProperties next = iterator.next();
                if(next == originalWeapon || next.getLevelRequired() > Main.getOnlineProfile().getLevel()) // Do not allow wrong level weapons or original weapon
                    iterator.remove();
                size--;
            }
        }

        private void updateForNewLayout() {
            this.selectedWeapon = ORIGINAL_WEAPON;
            this.originalWeapon = getLayouts()[selectedLayout].getWeapon(uniqueType);
            this.dirty = false;
        }

        @Override
        public void update(float deltaTime, boolean isSelected) {
            WeaponProperties weapon = getSelectedWeapon();

            setDisplayText(
                    getText()
                            .replaceAll("%weapontype%", uniqueType.getDisplayName())
                            .replaceAll("%weapon%", weapon != null ? weapon.getWeaponName() : Resources.getLang(LangEnum.NULL_WEAPON_NAME))
                            + (selectedWeapon == ORIGINAL_WEAPON ? " " + Resources.getLang(LangEnum.SETTED_WEAPON_SELECTED) : "")
            );
        }

        @Override
        public void doButtonAction(boolean positiveAction, int timesPressed) {
            selectedWeapon += (positiveAction ? 1 : -1);
            fixSelectedWeapon();
            dirty = true;
        }

        boolean saveToLayout() {
            if(dirty) {
                getLayouts()[selectedLayout].setWeapon(uniqueType, getSelectedWeapon());
                return true;
            }
            return false;
        }

        private void fixSelectedWeapon() {
            int shortestIndex = uniqueType.isRequired() ? ORIGINAL_WEAPON : (originalWeapon == null ? ORIGINAL_WEAPON : NULL_WEAPON);

            if(selectedWeapon < shortestIndex)
                selectedWeapon = validWeapons.size - 1;
            else if(selectedWeapon >= validWeapons.size)
                selectedWeapon = shortestIndex;
        }

        public WeaponProperties getSelectedWeapon() {
            WeaponProperties weapon = originalWeapon;
            if(selectedWeapon != ORIGINAL_WEAPON)
                weapon = (selectedWeapon == NULL_WEAPON ? null : validWeapons.get(selectedWeapon));

            return weapon;
        }
    }
}
