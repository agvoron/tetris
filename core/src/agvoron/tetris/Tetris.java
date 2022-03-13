package agvoron.tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import agvoron.tetris.ui.SettingsScreen;
import agvoron.tetris.ui.TetrisScreen;
import agvoron.tetris.ui.TitleScreen;

public class Tetris extends Game {
    /** Use only to switch screens */
    public static Tetris app;

    /** Use to create UI elements */
    public static Skin ui_skin;

    private static TitleScreen titleScreen;
    private static TetrisScreen tetrisScreen;
    private static SettingsScreen settingsScreen;

    @Override
    public void create() {
        // TODO loading bar here - use Asset Manager?
        app = this;
        ui_skin = new Skin(Gdx.files.internal("plain-james/plain-james-ui.json"));
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
}
