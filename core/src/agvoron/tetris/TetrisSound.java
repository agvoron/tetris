package agvoron.tetris;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

public class TetrisSound {

    public static Sound helperSoundRandomThunk() {
        switch (MathUtils.random(2)) {
            case 0:
                return Tetris.app.manager.get("thunk1.mp3", Sound.class);
            case 1:
                return Tetris.app.manager.get("thunk2.mp3", Sound.class);
            case 2:
                return Tetris.app.manager.get("thunk3.mp3", Sound.class);
            default:
                return null;
        }
    }

    public static Sound helperSoundRandomWhoosh() {
        switch (MathUtils.random(2)) {
            case 0:
                return Tetris.app.manager.get("whoosh1.mp3", Sound.class);
            case 1:
                return Tetris.app.manager.get("whoosh2.mp3", Sound.class);
            case 2:
                return Tetris.app.manager.get("whoosh3.mp3", Sound.class);
            default:
                return null;
        }
    }

}
