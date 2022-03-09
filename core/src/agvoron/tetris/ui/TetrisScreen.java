package agvoron.tetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import agvoron.tetris.Tetris;
import agvoron.tetris.game.Board;
import agvoron.tetris.game.Score;
import agvoron.tetris.game.Tetromino;

public class TetrisScreen implements Screen {

    private static final float HOR_BOARD_PAD = 200;
    private static final float VER_BOARD_PAD = 50;

    private Stage stage;
    private OrthographicCamera stageCam;
    private ShapeRenderer renderer; // TODO consider using Shape Drawer from LibGDX community
    private FPSLogger fps;

    private Label scoreText;

    private Board board;

    private float tileSize;
    private float startX;
    private float endX;
    private float startY;
    private float endY;

    private Tetromino currPiece;
    private float gravity;
    private float gravityTimer;
    private boolean softDropActive;

    public TetrisScreen() {
        stage = new Stage(new ScreenViewport());
        renderer = new ShapeRenderer();
        stageCam = (OrthographicCamera) stage.getViewport().getCamera();
        stageCam.setToOrtho(false);
        fps = new FPSLogger(59);

        board = new Board();
        // TODO if board too small, these go negative, fix that
        float tileSizeW = (Gdx.graphics.getWidth() - (2 * HOR_BOARD_PAD)) / board.getWidth();
        float tileSizeH = (Gdx.graphics.getHeight() - (2 * VER_BOARD_PAD)) / board.getHeight();
        tileSize = Math.min(tileSizeW, tileSizeH);

        if (tileSize == tileSizeW) {
            // constrained by width
            startX = HOR_BOARD_PAD;
            endX = Gdx.graphics.getWidth() - HOR_BOARD_PAD;
            startY = (Gdx.graphics.getHeight() / 2) - (board.getHeight() / 2 * tileSize);
            endY = (Gdx.graphics.getHeight() / 2) + (board.getHeight() / 2 * tileSize);

        } else {
            // constrained by height
            startX = (Gdx.graphics.getWidth() / 2) - (board.getWidth() / 2 * tileSize);
            endX = (Gdx.graphics.getWidth() / 2) + (board.getWidth() / 2 * tileSize);
            startY = VER_BOARD_PAD;
            endY = Gdx.graphics.getHeight() - VER_BOARD_PAD;
        }

        scoreText = new Label("Score: 0", Tetris.ui_skin);
        stage.addActor(scoreText);

        setupKeyControls();

        currPiece = new Tetromino(board);
        gravity = 0.5f;
        gravityTimer = 1f;
        Score.reset();
    }

    /**
     * Setup input handlers for keys
     */
    private void setupKeyControls() {
        stage.addListener(new InputListener() {

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
//                Gdx.app.log("Key", Input.Keys.toString(keycode));
                switch (keycode) {
                    case Input.Keys.SPACE:
                        int fallDistance = 0;
                        while (!currPiece.fall()) {
                            fallDistance++;
                        }
                        Score.hardDrop(fallDistance);
                        break;
                    case Input.Keys.DOWN:
                        softDropActive = true;
                        break;
                    case Input.Keys.RIGHT:
                        currPiece.translateRight();
                        break;
                    case Input.Keys.LEFT:
                        currPiece.translateLeft();
                        break;
                    case Input.Keys.Z:
                        currPiece.rotateLeft();
                        break;
                    case Input.Keys.X:
                        currPiece.rotateRight();
                        break;
                    case Input.Keys.C:
                        break;
                    case Input.Keys.A:
                        currPiece.rotateFlip();
                        break;
                }
                return super.keyDown(event, keycode);
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.DOWN:
                        softDropActive = false;
                        break;
                }
                return super.keyUp(event, keycode);
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
        Gdx.gl.glEnable(GL30.GL_BLEND);

        // draw Tetris board
        renderer.setProjectionMatrix(stageCam.combined);
        renderer.begin(ShapeType.Filled);

        // draw board squares
        for (int i = 0; i < board.getWidth(); i++) {
            float loopX = startX + (tileSize * i);
            for (int j = 0; j < board.getHeight(); j++) {
                float loopY = startY + (tileSize * j);
                renderer.setColor(board.getSquare(i, j).color);
                renderer.box(loopX, loopY, 0, tileSize, tileSize, 0);
            }
        }
        // draw tetromino
        int[] tetromino = currPiece.getTetromino();
        renderer.setColor(currPiece.getColor());
        for (int i = 0; i < tetromino.length; i += 2) {
            float loopX = startX + tileSize * tetromino[i];
            float loopY = startY + tileSize * tetromino[i + 1];
            renderer.box(loopX, loopY, 0, tileSize, tileSize, 0);
        }
        // draw board lines TODO top left pixel is missing...!?
        renderer.setColor(0, 0, 0, .8f);
        for (int i = 0; i < board.getWidth() + 1; i++) {
            float loopX = startX + (tileSize * i);
            renderer.rectLine(loopX, startY, loopX, endY, 1);
        }
        for (int i = 0; i < board.getHeight() + 1; i++) {
            float loopY = startY + (tileSize * i);
            renderer.rectLine(startX, loopY, endX, loopY, 1);
        }

        renderer.end();

        // draw UI
        stage.act();
        stage.draw();

        // game loop update
        gravityTimer += (softDropActive ? delta * 4 : delta);
        if (gravityTimer > gravity) {
            gravityTimer = 0;
            currPiece.fall();
            if (softDropActive) {
                Score.trickleSoftDrop();
            }
        }

        scoreText.setText("Score: " + Score.getScore());

        fps.log();
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
