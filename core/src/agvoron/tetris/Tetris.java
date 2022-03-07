package agvoron.tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import agvoron.tetris.ui.TitleScreen;

public class Tetris extends Game {
    /** use to create UI elements */
    static public Skin ui_skin;

    @Override
    public void create() {
        // TODO loading bar here - use Asset Manager?
        ui_skin = new Skin(Gdx.files.internal("plain-james/plain-james-ui.json"));
        this.setScreen(new TitleScreen());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        // TODO consider strategy for disposing screens
        ui_skin.dispose();
    }
}
