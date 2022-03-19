package agvoron.tetris.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import agvoron.tetris.Tetris;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        // window
        config.resizable = false;
        config.title = "Tetris";
        config.addIcon("logo_large.png", FileType.Internal);
        config.addIcon("logo_med.png", FileType.Internal);
        config.addIcon("logo_smol.png", FileType.Internal);

        // center
        config.x = -1;
        config.y = -1;

        // TODO graphics settings to make configurable
        config.width = 1080;
        config.height = 720;
        config.backgroundFPS = 60;
        config.foregroundFPS = 60;
        config.fullscreen = false;
        config.undecorated = false;
//      config.samples = 0;
//      config.vSyncEnabled = true;
        config.pauseWhenMinimized = true;

        new LwjglApplication(new Tetris(), config);
    }
}
