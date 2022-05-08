package agvoron.tetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import agvoron.tetris.Settings;
import agvoron.tetris.Tetris;
import agvoron.tetris.game.Board;
import agvoron.tetris.game.Score;
import agvoron.tetris.game.TetrisController;
import agvoron.tetris.game.Tetromino;

public class TetrisScreen implements Screen {

    private static final float HOR_BOARD_PAD = 50;
    private static final float VER_BOARD_PAD = 50;
    private static final float SIDE_PANEL_PAD = 25;

    private Stage stage;
    private OrthographicCamera stageCam;
    private SpriteBatch batch;
    private ShapeRenderer renderer; // TODO consider using Shape Drawer from LibGDX community
    private FPSLogger fps;

    private Table infoTable;
    private Label levelText;
    private Label scoreText;
    private Label pausedText;
    private TextButton resume;
    private TextButton restart;
    private TextButton backToTitle;

    private float tileSize;
    private float boardStartX;
    private float boardEndX;
    private float boardStartY;
    private float boardEndY;

    private float heldContainerStartX;
    private float heldContainerStartY;
    private float heldContainerEndX;
    private float heldContainerEndY;

    private float upcomingContainerStartX;
    private float upcomingContainerStartY;
    private float upcomingContainerEndX;
    private float upcomingContainerEndY;

    private Texture background;
    private Texture black;
    private Texture blue;
    private Texture darkblue;
    private Texture green;
    private Texture orange;
    private Texture purple;
    private Texture red;
    private Texture yellow;

    private TetrisController ctrlr;

    public TetrisScreen() {
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        batch.enableBlending();
        renderer = new ShapeRenderer();
        stageCam = (OrthographicCamera) stage.getViewport().getCamera();
        stageCam.setToOrtho(false);
        fps = new FPSLogger(59);

        background = Tetris.app.manager.get("background.png", Texture.class);
        black = Tetris.app.manager.get("black.png", Texture.class);
        blue = Tetris.app.manager.get("blue.png", Texture.class);
        darkblue = Tetris.app.manager.get("darkblue.png", Texture.class);
        green = Tetris.app.manager.get("green.png", Texture.class);
        orange = Tetris.app.manager.get("orange.png", Texture.class);
        purple = Tetris.app.manager.get("purple.png", Texture.class);
        red = Tetris.app.manager.get("red.png", Texture.class);
        yellow = Tetris.app.manager.get("yellow.png", Texture.class);

        ctrlr = new TetrisController(this);

        float tileSizeW = (Gdx.graphics.getWidth() - (2 * HOR_BOARD_PAD))
                / (ctrlr.getBoard().getWidth() + (2 * Settings.SIDE_PANEL_WIDTH));
        float tileSizeH = Math.min((Gdx.graphics.getHeight() - (2 * VER_BOARD_PAD)) / ctrlr.getBoard().getHeight(),
                (Gdx.graphics.getHeight() - (2 * VER_BOARD_PAD))
                        / (Settings.PANEL_PIECE_HEIGHT * Settings.NUMBER_UPCOMING_SHOWN));
        tileSize = Math.min(tileSizeW, tileSizeH);

        if (tileSize == tileSizeW) {
            // constrained by width
            boardStartX = HOR_BOARD_PAD;
            boardEndX = Gdx.graphics.getWidth() - HOR_BOARD_PAD;
            boardStartY = (Gdx.graphics.getHeight() / 2) - (ctrlr.getBoard().getHeight() / 2 * tileSize);
            boardEndY = (Gdx.graphics.getHeight() / 2) + (ctrlr.getBoard().getHeight() / 2 * tileSize);

        } else {
            // constrained by height
            boardStartX = (Gdx.graphics.getWidth() / 2) - (ctrlr.getBoard().getWidth() / 2 * tileSize);
            boardEndX = (Gdx.graphics.getWidth() / 2) + (ctrlr.getBoard().getWidth() / 2 * tileSize);
            boardStartY = VER_BOARD_PAD;
            boardEndY = Gdx.graphics.getHeight() - VER_BOARD_PAD;
        }

        float sidePanelPad = SIDE_PANEL_PAD;
        if (true) {
            sidePanelPad = tileSize;
        }

        heldContainerStartX = boardStartX - (tileSize * Settings.SIDE_PANEL_WIDTH) - sidePanelPad;
        heldContainerStartY = boardEndY - (tileSize * Settings.PANEL_PIECE_HEIGHT);
        heldContainerEndX = boardStartX - sidePanelPad;
        heldContainerEndY = boardEndY;

        upcomingContainerStartX = boardEndX + sidePanelPad;
        upcomingContainerStartY = boardEndY - (tileSize * Settings.PANEL_PIECE_HEIGHT * Settings.NUMBER_UPCOMING_SHOWN);
        upcomingContainerEndX = boardEndX + (tileSize * Settings.SIDE_PANEL_WIDTH) + sidePanelPad;
        upcomingContainerEndY = boardEndY;

        infoTable = new Table();
        infoTable.setSkin(Tetris.ui_skin);
        infoTable.setFillParent(true);
        infoTable.left();
        infoTable.bottom();
        infoTable.pad(5);
//        infoTable.debugAll();
        stage.addActor(infoTable);

        levelText = new Label("Level: 1", Tetris.ui_skin);
        scoreText = new Label("Score: 0", Tetris.ui_skin);
        pausedText = new Label("Paused.", Tetris.ui_skin);
        pausedText.setVisible(false);
        infoTable.add(levelText).padLeft(5);
        infoTable.add(scoreText).padLeft(15);
        infoTable.add(pausedText).padLeft(15);

        resume = new TextButton("Resume", Tetris.ui_skin);
        restart = new TextButton("Restart", Tetris.ui_skin);
        backToTitle = new TextButton("Back to Title", Tetris.ui_skin);
        resume.setVisible(false);
        restart.setVisible(false);
        backToTitle.setVisible(false);
        infoTable.add(resume).padLeft(5);
        infoTable.add(restart).padLeft(5);
        infoTable.add(backToTitle).padLeft(5);

        setupKeyControls();
        setupButtonHandlers();
    }

    /**
     * Setup input handlers for keys
     */
    private void setupKeyControls() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                ctrlr.handleKeyDown(keycode);
                return super.keyDown(event, keycode);
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                ctrlr.handleKeyUp(keycode);
                return super.keyUp(event, keycode);
            }

        });
    }

    /**
     * Set up listeners for pause menubuttons
     */
    private void setupButtonHandlers() {
        resume.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                helperResumeGame();
            }
        });

        restart.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ctrlr.resetGame();
                helperResumeGame();
            }
        });

        backToTitle.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ctrlr.resetGame();
                Tetris.app.openTitle();
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

        // these draw calls use SpriteBatch
        batch.begin();

        // draw background textures for board, held piece, upcoming pieces
        helperRenderBoard(ctrlr.getBoard(), boardStartX, boardStartY);
        helperRenderBoard(ctrlr.getHeldPieceBoard(), heldContainerStartX, heldContainerStartY);
        helperRenderBoard(ctrlr.getUpcomingPieceBoard(), upcomingContainerStartX, upcomingContainerStartY);

        // draw ghost piece tetromino
        helperRenderTetromino(ctrlr.getGhostPiece(), boardStartX, boardStartY, true);

        // draw tetromino textures for each
        helperRenderTetromino(ctrlr.getCurrPiece(), boardStartX, boardStartY, false);
        if (ctrlr.getHeldPiece() != null) {
            helperRenderTetromino(ctrlr.getHeldPiece(), heldContainerStartX,
                    heldContainerStartY - (Settings.PANEL_PIECE_HEIGHT * tileSize), false);
        }
        for (int i = 0; i < ctrlr.getUpcomingPieces().length; i++) {
            helperRenderTetromino(ctrlr.getUpcomingPieces()[i], upcomingContainerStartX,
                    upcomingContainerStartY - (Settings.PANEL_PIECE_HEIGHT * tileSize * (i + 1)), false);
        }

        batch.end();

        // draw board outlines and grid for each, uses ShapeRenderer
        renderer.setProjectionMatrix(stageCam.combined);
        renderer.begin(ShapeType.Filled);

        helperRenderBoardLines(ctrlr.getBoard(), boardStartX, boardStartY, boardEndX, boardEndY);
        helperRenderBoardLines(ctrlr.getHeldPieceBoard(), heldContainerStartX, heldContainerStartY, heldContainerEndX,
                heldContainerEndY);
        helperRenderBoardLines(ctrlr.getUpcomingPieceBoard(), upcomingContainerStartX, upcomingContainerStartY,
                upcomingContainerEndX, upcomingContainerEndY);

        renderer.end();

        // draw UI
        scoreText.setText("Score: " + Score.getScore());
        levelText.setText("Level: " + ctrlr.getLevel());

        stage.act();
        stage.draw();

        ctrlr.gameLoopUpdate(delta);

        fps.log();
    }

    private Texture helperSelectTexture(Color color, boolean isGhost) {
        if (isGhost) {
            return black;
        }
        if (color == Tetromino.BLUE) {
            return darkblue;
        } else if (color == Tetromino.GREEN) {
            return green;
        } else if (color == Tetromino.LIGHT_BLUE) {
            return blue;
        } else if (color == Tetromino.ORANGE) {
            return orange;
        } else if (color == Tetromino.PURPLE) {
            return purple;
        } else if (color == Tetromino.RED) {
            return red;
        } else if (color == Tetromino.YELLOW) {
            return yellow;
        } else {
            return background;
        }
    }

    // TODO might be fun to allow graphics config - plain colors or textures

    /**
     * Helper to draw board background, must be called in between batch.begin() and
     * end()
     */
    private void helperRenderBoard(Board b, float startX, float startY) {
        for (int i = 0; i < b.getWidth(); i++) {
            float loopX = startX + (tileSize * i);
            for (int j = 0; j < b.getHeight(); j++) {
                float loopY = startY + (tileSize * j);
//                renderer.setColor(b.getSquare(i, j).color);
//                renderer.box(loopX, loopY, 0, tileSize, tileSize, 0);
                batch.draw(helperSelectTexture(b.getSquare(i, j).color, false), loopX, loopY, tileSize, tileSize);
            }
        }
    }

    /** Helper to draw tetrominoes, call between batch.begin() and end() */
    private void helperRenderTetromino(Tetromino piece, float startX, float startY, boolean isGhost) {
        int[] tetromino = piece.getTetromino();
//        renderer.setColor(piece.getColor());
        for (int i = 0; i < tetromino.length; i += 2) {
            float loopX = startX + tileSize * tetromino[i];
            float loopY = startY + tileSize * tetromino[i + 1];
//            renderer.box(loopX, loopY, 0, tileSize, tileSize, 0);
            if (isGhost) {
                batch.setColor(1, 1, 1, 0.4f);
            }
            batch.draw(helperSelectTexture(piece.getColor(), isGhost), loopX, loopY, tileSize, tileSize);
            if (isGhost) {
                batch.setColor(Color.WHITE);
            }
        }
    }

    /**
     * Helper to draw board outlines and grid, must be called in between
     * renderer.begin() and end()
     */
    private void helperRenderBoardLines(Board b, float startX, float startY, float endX, float endY) {
        renderer.setColor(0, 0, 0, .8f);
        for (int i = 0; i < b.getWidth() + 1; i++) {
            float loopX = startX + (tileSize * i);
            // add 1 to endY... otherwise top left pixel is missing
            renderer.rectLine(loopX, startY, loopX, endY + 1, 1);
        }
        for (int i = 0; i < b.getHeight() + 1; i++) {
            float loopY = startY + (tileSize * i);
            renderer.rectLine(startX, loopY, endX, loopY, 1);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        helperPauseGame();
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

    public void helperPauseGame() {
        ctrlr.pauseGame();
        pausedText.setText("Paused.");
        pausedText.setVisible(true);
        resume.setVisible(true);
        restart.setVisible(true);
        backToTitle.setVisible(true);
    }

    public void helperResumeGame() {
        if (ctrlr.isGameLost())
            return;
        ctrlr.unpauseGame();
        pausedText.setVisible(false);
        resume.setVisible(false);
        restart.setVisible(false);
        backToTitle.setVisible(false);
    }

    public void helperEndGame() {
        ctrlr.pauseGame();
        ctrlr.loseGame();
        pausedText.setText("Game Over!");
        pausedText.setVisible(true);
        resume.setVisible(true);
        restart.setVisible(true);
        backToTitle.setVisible(true);
    }

}
