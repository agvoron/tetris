package agvoron.tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

import agvoron.tetris.ui.SettingsScreen;
import agvoron.tetris.ui.TetrisScreen;
import agvoron.tetris.ui.TitleScreen;

public class Tetris extends Game {

    private static final String SETTINGS_FILENAME = "settings.json";

    public static Tetris app;

    /** Use to create UI elements */
    public static Skin ui_skin;

    public static Settings settings;

    private static TitleScreen titleScreen;
    private static TetrisScreen tetrisScreen;
    private static SettingsScreen settingsScreen;

    public AssetManager manager;

    @Override
    public void create() {
        app = this;

        // prep to load resources
        manager = new AssetManager();
        manager.load("background.png", Texture.class);
        manager.load("blue.png", Texture.class);
        manager.load("darkblue.png", Texture.class);
        manager.load("green.png", Texture.class);
        manager.load("orange.png", Texture.class);
        manager.load("purple.png", Texture.class);
        manager.load("red.png", Texture.class);
        manager.load("yellow.png", Texture.class);
        manager.load("thunk1.mp3", Sound.class);
        manager.load("thunk2.mp3", Sound.class);
        manager.load("thunk3.mp3", Sound.class);
        manager.load("whoosh1.mp3", Sound.class);
        manager.load("whoosh2.mp3", Sound.class);
        manager.load("whoosh3.mp3", Sound.class);

        // resources required for title are not managed by AssetManager
        ui_skin = new Skin(Gdx.files.internal("plain-james/plain-james-ui.json"));
        try {
            // TODO using built-in serializer; learn about customizing it, and validation
            settings = new Json().fromJson(Settings.class, Gdx.files.local(SETTINGS_FILENAME));
        } catch (SerializationException e) {
            settings = new Settings();
        }

        titleScreen = new TitleScreen();
        settingsScreen = null;
        tetrisScreen = null;
        openTitle();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (titleScreen != null) {
            titleScreen.dispose();
        }
        if (settingsScreen != null) {
            settingsScreen.dispose();
        }
        if (tetrisScreen != null) {
            tetrisScreen.dispose();
        }
        ui_skin.dispose();
        manager.dispose();
    }

    public void openTitle() {
        setScreen(titleScreen);
    }

    public void openSettings() {
        // settings changes may require a fresh tetris screen
        if (tetrisScreen != null) {
            tetrisScreen.dispose();
            tetrisScreen = null;
        }
        // a fresh settings screen will be required either way
        if (settingsScreen != null) {
            settingsScreen.dispose();
            settingsScreen = null;
        }
        settingsScreen = new SettingsScreen();
        setScreen(settingsScreen);
    }

    public void openTetris() {
        if (tetrisScreen == null) {
            tetrisScreen = new TetrisScreen();
        }
        setScreen(tetrisScreen);
    }

    public void saveSettings() {
        settings.save(Gdx.files.local(SETTINGS_FILENAME));
    }
}
