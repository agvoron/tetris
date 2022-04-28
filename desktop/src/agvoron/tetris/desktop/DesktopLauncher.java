package agvoron.tetris.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import agvoron.tetris.Tetris;

public class DesktopLauncher {
    public static void main(String[] arg) {
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

        new Lwjgl3Application(new Tetris(), config);
    }
}
