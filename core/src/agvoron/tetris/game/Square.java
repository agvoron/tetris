package agvoron.tetris.game;

import com.badlogic.gdx.graphics.Color;

public class Square {

    public Color color;
    public boolean occupied;

    public Square() {
        color = new Color(0.75f, 0.75f, 0.75f, 1f);
        occupied = false;
    }

}
