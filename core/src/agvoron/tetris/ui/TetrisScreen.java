package agvoron.tetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
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
import agvoron.tetris.game.Square;
import agvoron.tetris.game.Tetromino;
import agvoron.tetris.game.Tetromino.Rotation;
import agvoron.tetris.game.Tetromino.Shape;

public class TetrisScreen implements Screen {

    private static final float HOR_BOARD_PAD = 50;
    private static final float VER_BOARD_PAD = 50;
    private static final float SIDE_PANEL_PAD = 25;
    private static final int SIDE_PANEL_WIDTH = 5;
    private static final int PANEL_PIECE_HEIGHT = 3;
    private static final int NUMBER_UPCOMING_SHOWN = 4;

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

    private Board board;
    private Board heldPieceContainer;
    private Board upcomingPiecesContainer;

    private Tetromino currPiece;
    private Tetromino heldPiece;
    private Tetromino[] upcomingPieces;
    private Tetromino ghostPiece;

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

    private float gravity;
    private float gravityTimer;
    private float hardDropRepeatDelay;
    private float hardDropTimer;
    private float levelScalar;
    private int level;
    private int placedCount;
    private int placedCountForLevelup;
    private boolean softDropActive;
    private boolean holdAvailable;
    private boolean isGamePaused;
    private boolean isLost;

    private Texture background;
    private Texture black;
    private Texture blue;
    private Texture darkblue;
    private Texture green;
    private Texture orange;
    private Texture purple;
    private Texture red;
    private Texture yellow;

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

        setupInitGameState();

        float tileSizeW = (Gdx.graphics.getWidth() - (2 * HOR_BOARD_PAD)) / (board.getWidth() + (2 * SIDE_PANEL_WIDTH));
        float tileSizeH = Math.min((Gdx.graphics.getHeight() - (2 * VER_BOARD_PAD)) / board.getHeight(),
                (Gdx.graphics.getHeight() - (2 * VER_BOARD_PAD)) / (PANEL_PIECE_HEIGHT * NUMBER_UPCOMING_SHOWN));
        tileSize = Math.min(tileSizeW, tileSizeH);

        if (tileSize == tileSizeW) {
            // constrained by width
            boardStartX = HOR_BOARD_PAD;
            boardEndX = Gdx.graphics.getWidth() - HOR_BOARD_PAD;
            boardStartY = (Gdx.graphics.getHeight() / 2) - (board.getHeight() / 2 * tileSize);
            boardEndY = (Gdx.graphics.getHeight() / 2) + (board.getHeight() / 2 * tileSize);

        } else {
            // constrained by height
            boardStartX = (Gdx.graphics.getWidth() / 2) - (board.getWidth() / 2 * tileSize);
            boardEndX = (Gdx.graphics.getWidth() / 2) + (board.getWidth() / 2 * tileSize);
            boardStartY = VER_BOARD_PAD;
            boardEndY = Gdx.graphics.getHeight() - VER_BOARD_PAD;
        }

        float sidePanelPad = SIDE_PANEL_PAD;
        if (true) {
            sidePanelPad = tileSize;
        }

        heldContainerStartX = boardStartX - (tileSize * SIDE_PANEL_WIDTH) - sidePanelPad;
        heldContainerStartY = boardEndY - (tileSize * PANEL_PIECE_HEIGHT);
        heldContainerEndX = boardStartX - sidePanelPad;
        heldContainerEndY = boardEndY;

        upcomingContainerStartX = boardEndX + sidePanelPad;
        upcomingContainerStartY = boardEndY - (tileSize * PANEL_PIECE_HEIGHT * NUMBER_UPCOMING_SHOWN);
        upcomingContainerEndX = boardEndX + (tileSize * SIDE_PANEL_WIDTH) + sidePanelPad;
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
     * Initial game state variables
     */
    private void setupInitGameState() {
        board = new Board();
        heldPieceContainer = new Board(SIDE_PANEL_WIDTH, PANEL_PIECE_HEIGHT);
        upcomingPiecesContainer = new Board(SIDE_PANEL_WIDTH, PANEL_PIECE_HEIGHT * NUMBER_UPCOMING_SHOWN);

        currPiece = new Tetromino(board);
        ghostPiece = new Tetromino(board, currPiece.getShape());
        helperPositionGhostPiece(ghostPiece);
        heldPiece = null;
        upcomingPieces = new Tetromino[NUMBER_UPCOMING_SHOWN];
        for (int i = 0; i < NUMBER_UPCOMING_SHOWN; i++) {
            upcomingPieces[i] = helperPositionForDisplay(new Tetromino(upcomingPiecesContainer));
        }

        gravity = 0.5f;
        gravityTimer = -1f;
        hardDropRepeatDelay = 0.12f;
        hardDropTimer = 0f;
        levelScalar = 1.2f;
        level = 1;
        placedCount = 0;
        placedCountForLevelup = 20;
        Score.reset();
        softDropActive = false;
        holdAvailable = true;
        isGamePaused = false;
        isLost = false;
    }

    /**
     * Setup input handlers for keys
     */
    private void setupKeyControls() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (isLost) {
                    return super.keyDown(event, keycode);
                }

                if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[8])) {
                    if (isGamePaused) {
                        helperResumeGame();
                    } else {
                        helperPauseGame();
                    }
                }

                if (isGamePaused) {
                    return super.keyDown(event, keycode);
                }

                if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[0])) {
                    if (hardDropTimer < hardDropRepeatDelay) {
                        // too soon after placing - stop accidental hard drop
                        return super.keyDown(event, keycode);
                    }
                    int fallDistance = 0;
                    while (!currPiece.fall()) {
                        fallDistance++;
                    }
                    helperPlacePiece();
                    Score.hardDrop(fallDistance);
                } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[1])) {
                    softDropActive = true;
                } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[2])) {
                    currPiece.translateRight();
                    helperPositionGhostPiece(ghostPiece);
                } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[3])) {
                    currPiece.translateLeft();
                    helperPositionGhostPiece(ghostPiece);
                } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[4])) {
                    currPiece.rotateRight();
                    helperPositionGhostPiece(ghostPiece);
                } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[5])) {
                    currPiece.rotateLeft();
                    helperPositionGhostPiece(ghostPiece);
                } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[6])) {
                    helperHoldPiece();
                } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[7])) {
                    currPiece.rotateFlip();
                    helperPositionGhostPiece(ghostPiece);
                }
                return super.keyDown(event, keycode);
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[1])) {
                    softDropActive = false;
                }
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
                helperResetGame();
            }
        });

        backToTitle.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                helperResetGame();
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
        helperRenderBoard(board, boardStartX, boardStartY);
        helperRenderBoard(heldPieceContainer, heldContainerStartX, heldContainerStartY);
        helperRenderBoard(upcomingPiecesContainer, upcomingContainerStartX, upcomingContainerStartY);

        // draw ghost piece tetromino
        helperRenderTetromino(ghostPiece, boardStartX, boardStartY, true);

        // draw tetromino textures for each
        helperRenderTetromino(currPiece, boardStartX, boardStartY, false);
        if (heldPiece != null) {
            helperRenderTetromino(heldPiece, heldContainerStartX, heldContainerStartY - (PANEL_PIECE_HEIGHT * tileSize),
                    false);
        }
        for (int i = 0; i < upcomingPieces.length; i++) {
            helperRenderTetromino(upcomingPieces[i], upcomingContainerStartX,
                    upcomingContainerStartY - (PANEL_PIECE_HEIGHT * tileSize * (i + 1)), false);
        }

        batch.end();

        // draw board outlines and grid for each, uses ShapeRenderer
        renderer.setProjectionMatrix(stageCam.combined);
        renderer.begin(ShapeType.Filled);

        helperRenderBoardLines(board, boardStartX, boardStartY, boardEndX, boardEndY);
        helperRenderBoardLines(heldPieceContainer, heldContainerStartX, heldContainerStartY, heldContainerEndX,
                heldContainerEndY);
        helperRenderBoardLines(upcomingPiecesContainer, upcomingContainerStartX, upcomingContainerStartY,
                upcomingContainerEndX, upcomingContainerEndY);

        renderer.end();

        // draw UI
        stage.act();
        stage.draw();

        // game loop update
        if (!isGamePaused) {
            gravityTimer += (softDropActive ? delta * 4 : delta) * Math.pow(levelScalar, level - 1);
            hardDropTimer += delta;
            while (gravityTimer > gravity) {
                gravityTimer -= gravity;
                if (currPiece.fall()) {
                    helperPlacePiece();
                }
                if (softDropActive) {
                    Score.trickleSoftDrop();
                }
            }

            scoreText.setText("Score: " + Score.getScore());
            levelText.setText("Level: " + level);
        }

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

    /**
     * Run this after currPiece.fall() returns true, to update the board with the
     * final location of currPiece, check for top-out, and reset state for a new
     * tetromino if all is well
     */
    private void helperPlacePiece() {
        helperSoundRandomThunk().play();

        int[] tetromino = currPiece.getTetromino();
        for (int i = 0; i < tetromino.length; i += 2) {
            if (tetromino[i + 1] >= board.getHeight()) {
                // topped out
                helperEndGame();
                return;
            }
            Square editSquare = board.getSquare(tetromino[i], tetromino[i + 1]);
            editSquare.color = currPiece.getColor();
            editSquare.occupied = true;
        }
        if (board.clearLines() > 0) {
            helperSoundRandomWhoosh().play();
        }
        gravityTimer = 0;
        hardDropTimer = 0;
        placedCount++;
        if (placedCount % placedCountForLevelup == 0) {
            level++;
        }
        holdAvailable = true;

        helperGrabUpcoming();
    }

    /** Send the current piece to the held piece side panel */
    private boolean helperHoldPiece() {
        if (!holdAvailable) {
            return true;
        }
        holdAvailable = false;
        Shape saveShape = currPiece.getShape();
        if (heldPiece == null) {
            helperGrabUpcoming();
        } else {
            currPiece = new Tetromino(board, heldPiece.getShape());
            ghostPiece = new Tetromino(board, currPiece.getShape());
            helperPositionGhostPiece(ghostPiece);
        }
        heldPiece = helperPositionForDisplay(new Tetromino(heldPieceContainer, saveShape));
        return false;
    }

    /** Helper to take a tetromino from the top of the upcoming pieces list */
    private void helperGrabUpcoming() {
        currPiece = new Tetromino(board, upcomingPieces[0].getShape());
        ghostPiece = new Tetromino(board, currPiece.getShape());
        helperPositionGhostPiece(ghostPiece);
        for (int i = 0; i < upcomingPieces.length - 1; i++) {
            upcomingPieces[i] = upcomingPieces[i + 1];
        }
        upcomingPieces[upcomingPieces.length - 1] = helperPositionForDisplay(new Tetromino(upcomingPiecesContainer));
    }

    /**
     * Helper - reset the ghost piece to the position/rotation of the curr piece,
     * then drop until it lands
     */
    private void helperPositionGhostPiece(Tetromino ghost) {
        ghost.teleportTo(currPiece);
        while (!ghost.fall()) {
        }
    }

    /** Helper to rotate and position display pieces for aesthetics only */
    private Tetromino helperPositionForDisplay(Tetromino piece) {
        switch (piece.getShape()) {
            case I:
                piece.setRotation(Rotation.N);
                piece.translateUp();
                break;
            case J:
                piece.setRotation(Rotation.E);
                piece.fall();
                break;
            case L:
                piece.setRotation(Rotation.W);
                piece.fall();
                piece.translateRight();
                break;
            case O:
                piece.setRotation(Rotation.N);
                break;
            case S:
                piece.setRotation(Rotation.E);
                break;
            case T:
                piece.setRotation(Rotation.E);
                piece.translateRight();
                break;
            case Z:
                piece.setRotation(Rotation.E);
                break;
        }
        return piece;
    }

    private void helperPauseGame() {
        isGamePaused = true;
        pausedText.setText("Paused.");
        pausedText.setVisible(true);
        resume.setVisible(true);
        restart.setVisible(true);
        backToTitle.setVisible(true);
    }

    private void helperResumeGame() {
        if (isLost)
            return;
        isGamePaused = false;
        pausedText.setVisible(false);
        resume.setVisible(false);
        restart.setVisible(false);
        backToTitle.setVisible(false);
    }

    private void helperEndGame() {
        isGamePaused = true;
        isLost = true;
        pausedText.setText("Game Over!");
        pausedText.setVisible(true);
        resume.setVisible(true);
        restart.setVisible(true);
        backToTitle.setVisible(true);
    }

    private void helperResetGame() {
        setupInitGameState();
        helperResumeGame();
    }

    private Sound helperSoundRandomThunk() {
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

    private Sound helperSoundRandomWhoosh() {
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
