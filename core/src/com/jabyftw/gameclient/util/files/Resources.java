package com.jabyftw.gameclient.util.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.screen.Animation;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.files.enums.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Rafael on 07/12/2014.
 */
public abstract class Resources {

    private static final HashMap<TextureEnum, Texture> textureMap = new HashMap<TextureEnum, Texture>();
    private static final HashMap<AnimationEnum, Animation> animationMap = new HashMap<AnimationEnum, Animation>();
    private static final HashMap<FontEnum, BitmapFont> bitmapFontMap = new HashMap<FontEnum, BitmapFont>();
    private static final HashMap<LangEnum, String> languageMap = new HashMap<LangEnum, String>();
    private static final HashMap<FilesEnum, FileHandle> commonFilesMap = new HashMap<FilesEnum, FileHandle>();
    private static final HashMap<String, FileHandle> mapFiles = new HashMap<String, FileHandle>();

    public static Animation getAnimation(AnimationEnum animationEnum) {
        return new Animation(animationMap.get(animationEnum));
    }

    public static void loadAnimations() {
        for(AnimationEnum animationEnum : AnimationEnum.values()) {
            animationMap.put(
                    animationEnum,
                    new Animation(
                            animationEnum.getFrameDelay(),
                            animationEnum.isLooping(),
                            TextureRegion.split(getTexture(animationEnum.getTextureEnum()), animationEnum.getTileWidth(), animationEnum.getTileHeight())[animationEnum.getSpriteSheetLine()]
                    )
            );
        }
    }

    private static Texture getTexture(TextureEnum textureEnum) {
        return textureMap.get(textureEnum);
    }

    public static void loadTextures() {
        for(TextureEnum textureEnum : TextureEnum.values()) {
            textureMap.put(textureEnum, new Texture(Gdx.files.internal(textureEnum.getFilePath())));
        }
    }

    public static BitmapFont getBitmapFont(FontEnum fontEnum) {
        return bitmapFontMap.get(fontEnum);
    }

    public static void loadBitmapFonts() {
        for(FontEnum fontEnum : FontEnum.values()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontEnum.getFilePath()));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = fontEnum.getSize();
            parameter.packer = null;
            parameter.flip = false;
            parameter.genMipMaps = false;
            parameter.minFilter = Texture.TextureFilter.Nearest;
            parameter.magFilter = Texture.TextureFilter.Nearest;
            bitmapFontMap.put(fontEnum, generator.generateFont(parameter));
            generator.dispose();
        }
    }

    public static String getLang(LangEnum langPath) {
        return languageMap.get(langPath);
    }

    // TODO: movie 'american beauty'

    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public static void loadLanguage(Language selected, boolean loadAll) {
        if(selected != Main.getOfflineProfile().getSelectedLanguage()) {
            Main.getOfflineProfile().setSelectedLanguage(selected);
            languageMap.clear();
        }

        Array<String> languagesLoaded = new Array<String>();
        try {
            for(Language language : (loadAll ? Language.values() : new Language[]{selected})) {
                Properties properties = new Properties();
                FileHandle languageFile = Resources.getFileHandle(FilesEnum.LANGUAGE_DIRECTORY).child(language.getFilePath());

                if(languageFile.exists()) {
                    properties.load(languageFile.reader("UTF-8"));
                    if(Constants.LANGUAGE_VERSION < 0 || Integer.parseInt(properties.getProperty(Constants.Util.LANG_VERSION_STRING)) != Constants.LANGUAGE_VERSION)
                        properties.clear(); // Recreate lang, lets not keep old values
                }

                for(LangEnum langEnum : LangEnum.values()) {

                    String string = properties.getProperty(langEnum.name().toLowerCase());
                    String defaultString = langEnum.getDefaultValue(language);

                    if(string == null || langEnum.isAlwaysUpdatedOnFile()) { // If shouldn't update every time, don't update
                        properties.put(langEnum.name().toLowerCase(), defaultString);
                        string = defaultString;
                    }
                    if(language == selected) // Just load to map if it is the selected language
                        languageMap.put(langEnum, string);
                }

                properties.setProperty(Constants.Util.LANG_VERSION_STRING, String.valueOf(Constants.LANGUAGE_VERSION));
                properties.store(languageFile.writer(false, "UTF-8"), "Comment? Why would I need that? Here's a random number: " + MathUtils.random());

                languagesLoaded.add(language.getDisplayName());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        System.out.println("Resources.loadLanguage { languages = " + Arrays.toString(languagesLoaded.toArray()) + " }");
    }

    public static void reloadMapsFromDirectory(FileHandle directory) {
        if(directory.exists() && directory.isDirectory()) {
            FileHandle[] list = directory.list();

            for(FileHandle fileHandle : list) {
                if(fileHandle.extension().equalsIgnoreCase("map")) {
                    mapFiles.put(fileHandle.nameWithoutExtension().toLowerCase(), fileHandle);
                } else {
                    System.out.println("Invalid extension: " + fileHandle.extension());
                }
            }

            Set<String> mapNames = Resources.getMapNames();
            System.out.println("Resources.reloadMapsFromDirectory { maps: " + Arrays.toString(mapNames.toArray()) + " }");
        }
    }

    public static Set<String> getMapNames() {
        return mapFiles.keySet();
    }

    public static Map loadFinishedMap(String mapName) {
        if(mapFiles.get(mapName.toLowerCase()) == null)
            throw new IllegalArgumentException("Invalid map name: " + mapName);
        return Map.readMap(mapFiles.get(mapName.toLowerCase()));
    }

    public static void loadCommonFiles() {
        for(FilesEnum filesEnum : FilesEnum.values()) {
            FileHandle fileHandle = Gdx.files.getFileHandle(filesEnum.getPath(), filesEnum.getType());
            if(filesEnum.shouldExist() && !fileHandle.exists())
                fileHandle.mkdirs();
            commonFilesMap.put(filesEnum, fileHandle);
        }
    }

    public static FileHandle getFileHandle(FilesEnum filesEnum) {
        return commonFilesMap.get(filesEnum);
    }

    public static void dispose() {
        animationMap.clear();
        for(Texture texture : textureMap.values()) {
            texture.dispose();
        }
        textureMap.clear();
        for(BitmapFont bitmapFont : bitmapFontMap.values()) {
            bitmapFont.dispose();
        }
        bitmapFontMap.clear();
    }

    public enum Language {

        ENGLISH("English", "/english.properties"),
        PORTUGUESE_BRAZIL("Português (BR)", "/portuguese-br.properties");

        private final String displayName, filePath;

        private Language(String displayName, String filePath) {
            this.displayName = displayName;
            this.filePath = filePath;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getFilePath() {
            return filePath;
        }

        public static Language getFromOrdinal(int ordinal) {
            {
                // Fix out of bounds
                if(ordinal >= values().length) ordinal = 0;
                else if(ordinal < 0) ordinal = values().length - 1;
            }
            for(Language language : values()) {
                if(language.ordinal() == ordinal) {
                    return language;
                }
            }
            return null;
        }
    }
}
