package agvoron.tetris;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class Settings {

    public static final String[] KEY_NAMES = { "Hard Drop", "Soft Drop", "Right", "Left", "Spin CW", "Spin CCW", "Hold",
            "Flip" };

    public HashMap<String, Integer> keys;

    /** Default settings */
    public Settings() {
        keys = new HashMap<String, Integer>();
        keys.put(KEY_NAMES[0], Input.Keys.SPACE);
        keys.put(KEY_NAMES[1], Input.Keys.DOWN);
        keys.put(KEY_NAMES[2], Input.Keys.RIGHT);
        keys.put(KEY_NAMES[3], Input.Keys.LEFT);
        keys.put(KEY_NAMES[4], Input.Keys.X);
        keys.put(KEY_NAMES[5], Input.Keys.Z);
        keys.put(KEY_NAMES[6], Input.Keys.C);
        keys.put(KEY_NAMES[7], Input.Keys.A);
    }

    public void save(FileHandle file) {
        Json json = new Json();
        json.setUsePrototypes(false);
        Gdx.files.local("settings.json").writeString(json.prettyPrint(this), false);
    }

}
