package agvoron.tetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import agvoron.tetris.Tetris;

public class TetrisScreen implements Screen {

    private Stage stage;

    public TetrisScreen() {
        stage = new Stage(new ScreenViewport());

        Label welcome = new Label("Welcome!", Tetris.ui_skin);
        stage.addActor(welcome);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Gdx.app.log("Switch", "ok");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
