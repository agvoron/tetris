package agvoron.tetris.game;

import com.badlogic.gdx.Gdx;

import agvoron.tetris.Settings;
import agvoron.tetris.Tetris;
import agvoron.tetris.TetrisSound;
import agvoron.tetris.game.Tetromino.Rotation;
import agvoron.tetris.game.Tetromino.Shape;
import agvoron.tetris.ui.TetrisScreen;

/**
 * Class to package up all the 'non-graphical' stuff that was building up in
 * TetrisScreen.
 * Not following a rigorous design here but loosely inspired by MVC architecture
 * "Model" is mostly in the Board/Tetromino/Score classes, "View" is all in
 * TetrisScreen, "Controller" stuff is here.
 * Includes:
 * - Managing the state (Board and all tetris stuff) (TetrisScreen should only
 * modify state via this guy)
 * - Input handling (Scene2d inputs go straight here ideally, unless the
 * handling is super simple and OK to leave in TetrisScreen, or directly
 * UI-related e.g. change screens, bring up a menu)
 * Does not include:
 * - Anything dependent on non- (usefully) mockable libGDX/etc. backend things
 * A secondary goal to the semantic View/Controller separation is to make this
 * class fully testable in JUnit.
 * 
 * I think these are good rules for me to use in simple game projects in the
 * future.
 */
public class TetrisController {

    private TetrisScreen view;

    private Board board;
    private Board heldPieceContainer;
    private Board upcomingPiecesContainer;

    private Tetromino currPiece;
    private Tetromino heldPiece;
    private Tetromino[] upcomingPieces;
    private Tetromino ghostPiece;

    private float gravity;
    private float gravityTimer;
    private float hardDropRepeatDelay;
    private float hardDropTimer;
    private float levelScalar;
    private int level;
    private int placedCount;
    private int placedCountForLevelup;
    private float translateRepeatDelay;
    private float translateRightRepeatTimer;
    private float translateLeftRepeatTimer;
    private float movementBeforePlaceDelay;
    private float lastMovedTimer;
    private boolean isLanded;
    private float landedMaxPlaceDelay;
    private float landedTimer;
    private boolean softDropActive;
    private boolean holdAvailable;
    private boolean isGamePaused;
    private boolean isLost;

    public TetrisController(TetrisScreen screen) {
        view = screen;
        resetGame();
    }

    public Board getBoard() {
        return board;
    }

    public Board getHeldPieceBoard() {
        return heldPieceContainer;
    }

    public Board getUpcomingPieceBoard() {
        return upcomingPiecesContainer;
    }

    public Tetromino getCurrPiece() {
        return currPiece;
    }

    public Tetromino getHeldPiece() {
        return heldPiece;
    }

    public Tetromino getGhostPiece() {
        return ghostPiece;
    }

    public Tetromino[] getUpcomingPieces() {
        return upcomingPieces;
    }

    public int getLevel() {
        return level;
    }

    public boolean isGamePaused() {
        return isGamePaused;
    }

    public void pauseGame() {
        isGamePaused = true;
    }

    public void unpauseGame() {
        isGamePaused = false;
    }

    public boolean isGameLost() {
        return isLost;
    }

    public void loseGame() {
        isLost = true;
    }

    /**
     * Initial game state variables
     */
    public void resetGame() {
        board = new Board();
        heldPieceContainer = new Board(Settings.SIDE_PANEL_WIDTH, Settings.PANEL_PIECE_HEIGHT);
        upcomingPiecesContainer = new Board(Settings.SIDE_PANEL_WIDTH,
                Settings.PANEL_PIECE_HEIGHT * Settings.NUMBER_UPCOMING_SHOWN);

        currPiece = new Tetromino(board);
        ghostPiece = new Tetromino(board, currPiece.getShape());
        positionGhostPiece(ghostPiece);
        heldPiece = null;
        upcomingPieces = new Tetromino[Settings.NUMBER_UPCOMING_SHOWN];
        for (int i = 0; i < Settings.NUMBER_UPCOMING_SHOWN; i++) {
            upcomingPieces[i] = positionForDisplay(new Tetromino(upcomingPiecesContainer));
        }

        // TODO pull out tweakables/configurables from this jumble
        gravity = 0.5f;
        gravityTimer = -1f;
        hardDropRepeatDelay = 0.2f;
        hardDropTimer = 0f;
        translateRepeatDelay = 0.16f;
        translateRightRepeatTimer = 0f;
        translateLeftRepeatTimer = 0f;
        movementBeforePlaceDelay = 0.4f;
        lastMovedTimer = 0f;
        landedMaxPlaceDelay = 2.0f;
        landedTimer = 0f;
        isLanded = false;
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

    public void handleKeyDown(int keycode) {
        if (isLost) {
            return;
        }

        if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[8])) {
            // TODO weird junction between controller/screen - these methods have to call
            // back to it
            if (isGamePaused) {
                if (isLost)
                    return;
                isGamePaused = false;
                view.resumeGameUI();
            } else {
                isGamePaused = true;
                view.pauseGameUI();
            }
        }

        if (isGamePaused) {
            return;
        }

        if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[0])) {
            if (hardDropTimer < hardDropRepeatDelay) {
                // too soon after placing - stop accidental hard drop
                return;
            }
            int fallDistance = 0;
            while (!currPiece.fall()) {
                fallDistance++;
            }
            placePiece();
            Score.hardDrop(fallDistance);
        } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[1])) {
            softDropActive = true;
            lastMovedTimer = 0f;
        } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[2])) {
            if (!currPiece.translateRight()) {
                lastMovedTimer = 0f;
            }
            translateRightRepeatTimer = 0f;
        } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[3])) {
            if (!currPiece.translateLeft()) {
                lastMovedTimer = 0f;
            }
            translateLeftRepeatTimer = 0f;
        } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[4])) {
            if (!currPiece.rotateRight()) {
                lastMovedTimer = 0f;
            } else if (!currPiece.rotateRightBump()) {
                lastMovedTimer = 0f;
            }
        } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[5])) {
            if (!currPiece.rotateLeft()) {
                lastMovedTimer = 0f;
            } else if (!currPiece.rotateLeftBump()) {
                lastMovedTimer = 0f;
            }
        } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[6])) {
            holdPiece();
        } else if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[7])) {
            if (!currPiece.rotateFlip()) {
                lastMovedTimer = 0f;
            } else if (!currPiece.rotateFlipBump()) {
                lastMovedTimer = 0f;
            }
        }

        positionGhostPiece(ghostPiece);
    }

    public void handleKeyUp(int keycode) {
        if (keycode == Tetris.settings.keys.get(Settings.KEY_NAMES[1])) {
            softDropActive = false;
        }
    }

    public void gameLoopUpdate(float delta) {
        if (isGamePaused) {
            return;
        }
        gameLoopKeyboardUpdate(delta);
        gravityTimer += (softDropActive ? delta * 4 : delta) * Math.pow(levelScalar, level - 1);
        hardDropTimer += delta;
        lastMovedTimer += delta;

        while (gravityTimer > gravity) {
            gravityTimer -= gravity;
            if (!currPiece.fall()) {
                isLanded = false;
                landedTimer = 0f;
                if (softDropActive) {
                    Score.trickleSoftDrop();
                }
            } else {
                isLanded = true;
            }
        }

        if (isLanded) {
            landedTimer += delta;
            if (lastMovedTimer > movementBeforePlaceDelay || landedTimer > landedMaxPlaceDelay) {
                placePiece();
            }
        }

    }

    private void gameLoopKeyboardUpdate(float delta) {
        if (isLost || isGamePaused) {
            return;
        }
        // process held keys (event handers handle key up/down)
        if (Gdx.input.isKeyPressed(Tetris.settings.keys.get(Settings.KEY_NAMES[2]))
                && Gdx.input.isKeyPressed(Tetris.settings.keys.get(Settings.KEY_NAMES[3]))) {
            // both keys pressed... prevent the back & forth
            translateRightRepeatTimer = 0;
            translateLeftRepeatTimer = 0;
            return;
        }
        if (Gdx.input.isKeyPressed(Tetris.settings.keys.get(Settings.KEY_NAMES[2]))) {
            translateRightRepeatTimer += delta;
            while (translateRightRepeatTimer > translateRepeatDelay) {
                if (!currPiece.translateRight()) {
                    lastMovedTimer = 0f;
                }
                positionGhostPiece(ghostPiece);
                translateRightRepeatTimer -= translateRepeatDelay;
            }
        }
        if (Gdx.input.isKeyPressed(Tetris.settings.keys.get(Settings.KEY_NAMES[3]))) {
            translateLeftRepeatTimer += delta;
            while (translateLeftRepeatTimer > translateRepeatDelay) {
                if (!currPiece.translateLeft()) {
                    lastMovedTimer = 0f;
                }
                positionGhostPiece(ghostPiece);
                translateLeftRepeatTimer -= translateRepeatDelay;
            }
        }
    }

    /**
     * Run this after currPiece.fall() returns true, to update the board with the
     * final location of currPiece, check for top-out, and reset state for a new
     * tetromino if all is well
     */
    private void placePiece() {
        TetrisSound.randomThunk().play();

        int[] tetromino = currPiece.getTetromino();
        for (int i = 0; i < tetromino.length; i += 2) {
            if (tetromino[i + 1] >= board.getHeight()) {
                // topped out
                // TODO another weird junction between controller/screen
                isGamePaused = true;
                isLost = true;
                view.endGameUI();
                return;
            }
            Square editSquare = board.getSquare(tetromino[i], tetromino[i + 1]);
            editSquare.color = currPiece.getColor();
            editSquare.occupied = true;
        }
        if (board.clearLines() > 0) {
            TetrisSound.randomWhoosh().play();
        }
        gravityTimer = 0;
        hardDropTimer = 0;
        lastMovedTimer = 0f;
        placedCount++;
        if (placedCount % placedCountForLevelup == 0) {
            level++;
        }
        holdAvailable = true;
        isLanded = false;

        grabUpcoming();
    }

    /** Send the current piece to the held piece side panel */
    private boolean holdPiece() {
        if (!holdAvailable) {
            return true;
        }
        holdAvailable = false;
        Shape saveShape = currPiece.getShape();
        if (heldPiece == null) {
            grabUpcoming();
        } else {
            currPiece = new Tetromino(board, heldPiece.getShape());
            ghostPiece = new Tetromino(board, currPiece.getShape());
            positionGhostPiece(ghostPiece);
        }
        heldPiece = positionForDisplay(new Tetromino(heldPieceContainer, saveShape));
        return false;
    }

    /** Helper to take a tetromino from the top of the upcoming pieces list */
    private void grabUpcoming() {
        currPiece = new Tetromino(board, upcomingPieces[0].getShape());
        ghostPiece = new Tetromino(board, currPiece.getShape());
        positionGhostPiece(ghostPiece);
        for (int i = 0; i < upcomingPieces.length - 1; i++) {
            upcomingPieces[i] = upcomingPieces[i + 1];
        }
        upcomingPieces[upcomingPieces.length - 1] = positionForDisplay(new Tetromino(upcomingPiecesContainer));
    }

    /**
     * Helper - reset the ghost piece to the position/rotation of the curr piece,
     * then drop until it lands
     */
    private void positionGhostPiece(Tetromino ghost) {
        ghost.teleportTo(currPiece);
        while (!ghost.fall()) {
        }
    }

    /** Helper to rotate and position display pieces for aesthetics only */
    private Tetromino positionForDisplay(Tetromino piece) {
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

}
