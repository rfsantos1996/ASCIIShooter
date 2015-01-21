package com.jabyftw.gameclient.util.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.screen.Animation;
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

    public static final String LANG_VERSION_STRING = "LangVersion";

    private static HashMap<TextureEnum, Texture> textureMap = new HashMap<TextureEnum, Texture>();
    private static HashMap<AnimationEnum, Animation> animationMap = new HashMap<AnimationEnum, Animation>();
    private static HashMap<FontEnum, BitmapFont> bitmapFontMap = new HashMap<FontEnum, BitmapFont>();
    private static HashMap<LangEnum, String> languageMap = new HashMap<LangEnum, String>();
    private static HashMap<FilesEnum, FileHandle> commonFilesMap = new HashMap<FilesEnum, FileHandle>();
    private static HashMap<String, FileHandle> mapFiles = new HashMap<String, FileHandle>();

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

    public static Texture getTexture(TextureEnum textureEnum) {
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

    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public static void loadLanguage(Language selected) {
        if(selected != Main.getOfflineProfile().getSelectedLanguage()) {
            Main.getOfflineProfile().setSelectedLanguage(selected);
            languageMap.clear();
        }

        Properties properties = new Properties();
        FileHandle langFile = Resources.getFileHandle(FilesEnum.LANGUAGE_DIRECTORY).child(selected.getFilePath());
        try {

            if(langFile.exists()) {
                properties.load(langFile.read());
                if(LangEnum.LANG_VERSION < 0 || Integer.parseInt(properties.getProperty(LANG_VERSION_STRING)) != LangEnum.LANG_VERSION)
                    properties.clear(); // Recreate lang, lets not keep old values
            }

            for(LangEnum strings : LangEnum.values()) {
                String property = properties.getProperty(strings.name().toLowerCase());
                String defaultValue = strings.getDefaultValue(selected);
                if(property != null && !strings.isAlwaysUpdatedOnFile()) {
                    languageMap.put(strings, property);
                } else {
                    properties.put(strings.name().toLowerCase(), defaultValue);
                    languageMap.put(strings, defaultValue);
                }
            }

            properties.setProperty(LANG_VERSION_STRING, String.valueOf(LangEnum.LANG_VERSION));
            properties.store(langFile.writer(false), "Comment? Why would I need that? Here's a random number: " + MathUtils.random());

        } catch(IOException e) {
            e.printStackTrace();
        }
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
            System.out.println("Loaded maps: " + Arrays.toString(mapNames.toArray()));
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
        PORTUGUESE_BRAZIL("Portugues (BR)", "/portuguese-br.properties");

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
