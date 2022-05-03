package agvoron.tetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import agvoron.tetris.Tetris;

public class TitleScreen implements Screen {

    private Stage stage;

    private Label loading;
    private TextButton play;
    private TextButton settings;
    private TextButton quit;

    private boolean isLoading;

    public TitleScreen() {
        stage = new Stage(new ScreenViewport());

        Table table = new Table();

        Label title = new Label("Tetris", Tetris.ui_skin);
        table.add(title);

        table.row();

        loading = new Label("Loading... 0%", Tetris.ui_skin);
        table.add(loading);

        table.row();

        play = new TextButton("Play", Tetris.ui_skin);
        play.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tetris.app.openTetris();
            }
        });
        table.add(play);
        play.setDisabled(true);

        table.row();

        settings = new TextButton("Settings", Tetris.ui_skin);
        settings.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tetris.app.openSettings();
            }
        });
        table.add(settings);
        settings.setDisabled(true);

        table.row();

        quit = new TextButton("Quit", Tetris.ui_skin);
        quit.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        table.add(quit);

        table.setSkin(Tetris.ui_skin);
        table.setFillParent(true);
        table.center();
        table.top();
        table.pad(70);
//        table.debugAll();
        stage.addActor(table);

        isLoading = true;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (isLoading) {
            if (Tetris.app.manager.update()) {
                loading.setText("");
                play.setDisabled(false);
                settings.setDisabled(false);
                isLoading = false;
            } else {
                loading.setText("Loading... " + Math.round(Tetris.app.manager.getProgress() * 100) + "%");
            }
        }
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
