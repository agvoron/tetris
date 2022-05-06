package agvoron.tetris.ui;

import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import agvoron.tetris.Tetris;

public class SettingsScreen implements Screen {

    private Stage stage;

    private String uiStateChoosingKey;
    private TextButton choosingKeyButton;

    public SettingsScreen() {
        stage = new Stage(new ScreenViewport());
        uiStateChoosingKey = null;

        Table table = new Table();

        Label title = new Label("Settings", Tetris.ui_skin);
        table.add(title);

        table.row();

        // TODO would be nice to list the keys in fixed order instead of saved order
        // validation ^ of the input file
        for (final Entry<String, Integer> entry : Tetris.settings.keys.entrySet()) {
            Label keyName = new Label(entry.getKey(), Tetris.ui_skin);
            table.add(keyName);
            final TextButton keyButton = new TextButton(Input.Keys.toString(entry.getValue()), Tetris.ui_skin);
            keyButton.addListener(new InputListener() {

                private TextButton myButton = keyButton;

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    uiStateChoosingKey = entry.getKey();
                    choosingKeyButton = myButton;
                    choosingKeyButton.setText("Press Key...");
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
            });
            table.add(keyButton);
            table.row();
        }

        TextButton save = new TextButton("Save", Tetris.ui_skin);
        save.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Tetris.app.saveSettings();
                Tetris.app.openTitle();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.add(save);

        table.setSkin(Tetris.ui_skin);
        table.setFillParent(true);
        table.center();
        table.top();
        table.pad(70);
//        table.debugAll();
        stage.addActor(table);

        setupKeyHandlers();
    }

    private void setupKeyHandlers() {
        stage.addListener(new InputListener() {

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (uiStateChoosingKey != null) {
                    Tetris.settings.keys.put(uiStateChoosingKey, keycode);
                    choosingKeyButton.setText(Input.Keys.toString(keycode));
                    choosingKeyButton = null;
                    uiStateChoosingKey = null;
                }
                return super.keyDown(event, keycode);
            }

        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
