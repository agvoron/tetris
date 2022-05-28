package agvoron.tetris.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;

import agvoron.tetris.Tetris;

public class DesktopLauncher {
    private static Tetris tetrisApp;
    // private static Lwjgl3Window window;

    public static void main(String[] arg) {
        tetrisApp = new Tetris();

        // TODO review LWJGL3 config options - for now ported from LWJGL2
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // window
        config.setResizable(false);
        config.setTitle("Tetris");
        config.setWindowIcon("logo_128.png", "logo_32.png", "logo_16.png");

        // center
        config.setWindowPosition(-1, -1);

        // TODO graphics settings to make configurable
        config.setWindowedMode(1080, 720);
        config.setForegroundFPS(60);
        // config.setFullscreenMode(null); // TODO unsure how to set fullscreen off/on
        // config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        config.setDecorated(true);
//      config.samples = 0;
//      config.vSyncEnabled = true;
//        config.pauseWhenMinimized = true; // TODO option removed

        // TODO learn about what I can do with the window and windowadapter
        // iconifyWindow to replace pauseWhenMinimized?
        config.setWindowListener(new Lwjgl3WindowAdapter() {

            @Override
            public void created(Lwjgl3Window window) {
                super.created(window);
                // this.window = window;
            }

            @Override
            public void focusLost() {
                // TODO figure out if this is the same thread - do I need to postRunnable?
                tetrisApp.pause();
            }

        });

        new Lwjgl3Application(tetrisApp, config);
    }
}
