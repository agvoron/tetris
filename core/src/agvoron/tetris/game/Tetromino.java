package agvoron.tetris.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class Tetromino {

    private Board board;

    private Shape shape;
    private Rotation rotation;

    /** Bottom left corner of shape in board coordinates */
    private int rootX;
    /** Bottom left corner of shape in board coordinates */
    private int rootY;
    /** Board coordinates of each square */
    private int[] tetromino;

    public static final Color LIGHT_BLUE = new Color(0.3f, 1f, 1f, 1f);
    public static final Color YELLOW = new Color(1f, 0.9f, 0.25f, 1f);
    public static final Color PURPLE = new Color(1f, 0.2f, 0.9f, 1f);
    public static final Color BLUE = new Color(0.2f, 0.2f, 1f, 1f);
    public static final Color ORANGE = new Color(1f, 0.6f, 0.2f, 1f);
    public static final Color GREEN = new Color(0.2f, 0.8f, 0.15f, 1f);
    public static final Color RED = new Color(0.9f, 0.15f, 0.15f, 1f);

    public enum Shape {
        I(LIGHT_BLUE) {
            @Override
            public int getStartingX(int boardWidth) {
                return boardWidth / 2 - 2;
            };
        },
        O(YELLOW), T(PURPLE), J(BLUE), L(ORANGE), S(GREEN), Z(RED);

        private final Color shapeColor;

        private Shape(Color shapeColor) {
            this.shapeColor = shapeColor;
        }

        public Color getColor() {
            return shapeColor;
        }

        /** The x position on the board to properly center this shape. */
        public int getStartingX(int boardWidth) {
            return boardWidth / 2 - 1;
        }
    }

    public enum Rotation {
        N {
            @Override
            public Rotation left() {
                return W;
            };
        },

        E, S,

        W {
            @Override
            public Rotation right() {
                return N;
            };
        };

        public Rotation right() {
            return values()[ordinal() + 1];
        }

        public Rotation left() {
            return values()[ordinal() - 1];
        }
    }

    /** Spawn tetromino with random shape */
    public Tetromino(Board board) {
        this.board = board;
        spawnRandom();
    }

    /** Spawn tetromino with given shape */
    public Tetromino(Board board, Shape shape) {
        this.board = board;
        spawnAsShape(shape);
    }

    private void spawnRandom() {
        spawnAsShape(Shape.values()[MathUtils.random(Shape.values().length - 1)]);
    }

    private void spawnAsShape(Shape shape) {
        this.shape = shape;
        rotation = Rotation.N;
        rootX = shape.getStartingX(board.getWidth());
        rootY = board.getHeight();
        refreshCoordinates();
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return shape.getColor();
    }

    /** @return board coordinates of this tetromino as {x1, y1, x2, y2, ... } */
    public int[] getTetromino() {
        return tetromino;
    }

    /** @return true if the translation would go out-of-bounds */
    public boolean translateRight() {
        rootX += 1;
        refreshCoordinates();
        if (testForHit()) {
            rootX -= 1;
            refreshCoordinates();
            return true;
        }
        return false;
    }

    /** @return true if the translation would go out-of-bounds */
    public boolean translateLeft() {
        rootX -= 1;
        refreshCoordinates();
        if (testForHit()) {
            rootX += 1;
            refreshCoordinates();
            return true;
        }
        return false;
    }

    /** @return true if the translation would go out-of-bounds */
    public boolean translateUp() {
        rootY += 1;
        refreshCoordinates();
        if (testForHit()) {
            rootY -= 1;
            refreshCoordinates();
            return true;
        }
        return false;
    }

    /** @return true if the rotation would go out-of-bounds */
    public boolean setRotation(Rotation r) {
        Rotation saved = rotation;
        rotation = r;
        refreshCoordinates();
        if (testForHit()) {
            rotation = saved;
            refreshCoordinates();
            return true;
        }
        return false;
    }

    /** @return true if the rotation would go out-of-bounds */
    public boolean rotateRight() {
        rotation = rotation.right();
        refreshCoordinates();
        if (testForHit()) {
            rotation = rotation.left();
            refreshCoordinates();
            return true;
        }
        return false;
    }

    /** @return true if the rotation would go out-of-bounds */
    public boolean rotateLeft() {
        rotation = rotation.left();
        refreshCoordinates();
        if (testForHit()) {
            rotation = rotation.right();
            refreshCoordinates();
            return true;
        }
        return false;
    }

    /** @return true if the rotation would go out-of-bounds */
    public boolean rotateFlip() {
        rotation = rotation.left();
        rotation = rotation.left();
        refreshCoordinates();
        if (testForHit()) {
            rotation = rotation.right();
            rotation = rotation.right();
            refreshCoordinates();
            return true;
        }
        return false;
    }

    /** @return true if the translation would go out-of-bounds */
    public boolean fall() {
        rootY -= 1;
        refreshCoordinates();
        if (testForHit()) {
            // we have landed
            rootY += 1;
            refreshCoordinates();
            return true;
        }
        return false;
    }

    private boolean testForHit() {
        for (int i = 0; i < tetromino.length; i += 2) {
            int x = tetromino[i];
            int y = tetromino[i + 1];
            if (x < 0 || y < 0 || x >= board.getWidth()) {
                return true;
            }
            if (y >= board.getHeight()) {
                continue; // it's OK to be above the board
            }
            Square square = board.getSquare(x, y);
            if (square.occupied) {
                return true;
            }
        }
        return false;
    }

    private void refreshCoordinates() {
        switch (shape) {
            case I:
                coordsI();
                break;
            case J:
                coordsJ();
                break;
            case L:
                coordsL();
                break;
            case O:
                // no rotation
                tetromino = new int[] { rootX, rootY, rootX + 1, rootY, rootX, rootY + 1, rootX + 1, rootY + 1 };
                break;
            case T:
                coordsT();
                break;
            case S:
                coordsS();
                break;
            case Z:
                coordsZ();
                break;
        }
    }

    private void coordsI() {
        switch (rotation) {
            case N:
                tetromino = new int[] { rootX, rootY, rootX + 1, rootY, rootX + 2, rootY, rootX + 3, rootY };
                break;
            case E:
                tetromino = new int[] { rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 1, rootY + 2, rootX + 1,
                        rootY + 3 };
                break;
            case S:
                // duplicate N
                tetromino = new int[] { rootX, rootY, rootX + 1, rootY, rootX + 2, rootY, rootX + 3, rootY };
                break;
            case W:
                // duplicate E
                tetromino = new int[] { rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 1, rootY + 2, rootX + 1,
                        rootY + 3 };
                break;
        }
    }

    private void coordsJ() {
        switch (rotation) {
            case N:
                tetromino = new int[] { rootX, rootY, rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 1, rootY + 2 };
                break;
            case E:
                tetromino = new int[] { rootX, rootY + 1, rootX, rootY + 2, rootX + 1, rootY + 1, rootX + 2,
                        rootY + 1 };
                break;
            case S:
                tetromino = new int[] { rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 1, rootY + 2, rootX + 2,
                        rootY + 2 };
                break;
            case W:
                tetromino = new int[] { rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 2, rootY + 1, rootX + 2,
                        rootY };
                break;
        }
    }

    private void coordsL() {
        switch (rotation) {
            case N:
                tetromino = new int[] { rootX, rootY, rootX + 1, rootY, rootX, rootY + 1, rootX, rootY + 2 };
                break;
            case E:
                tetromino = new int[] { rootX - 1, rootY, rootX - 1, rootY + 1, rootX, rootY + 1, rootX + 1,
                        rootY + 1 };
                break;
            case S:
                tetromino = new int[] { rootX, rootY, rootX, rootY + 1, rootX, rootY + 2, rootX - 1, rootY + 2 };
                break;
            case W:
                tetromino = new int[] { rootX - 1, rootY + 1, rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 1,
                        rootY + 2 };
                break;
        }
    }

    private void coordsT() {
        switch (rotation) {
            case N:
                tetromino = new int[] { rootX, rootY, rootX, rootY + 1, rootX + 1, rootY + 1, rootX, rootY + 2 };
                break;
            case E:
                tetromino = new int[] { rootX, rootY, rootX, rootY + 1, rootX + 1, rootY + 1, rootX - 1, rootY + 1 };
                break;
            case S:
                tetromino = new int[] { rootX, rootY, rootX, rootY + 1, rootX - 1, rootY + 1, rootX, rootY + 2 };
                break;
            case W:
                tetromino = new int[] { rootX, rootY + 1, rootX + 1, rootY + 1, rootX - 1, rootY + 1, rootX,
                        rootY + 2 };
                break;
        }
    }

    private void coordsS() {
        switch (rotation) {
            case N:
                tetromino = new int[] { rootX + 1, rootY, rootX + 1, rootY + 1, rootX, rootY + 1, rootX, rootY + 2 };
                break;
            case E:
                tetromino = new int[] { rootX, rootY, rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 2, rootY + 1 };
                break;
            case S:
                tetromino = new int[] { rootX + 1, rootY, rootX + 1, rootY + 1, rootX, rootY + 1, rootX, rootY + 2 };
                break;
            case W:
                tetromino = new int[] { rootX, rootY, rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 2, rootY + 1 };
                break;
        }
    }

    private void coordsZ() {
        switch (rotation) {
            case N:
                tetromino = new int[] { rootX, rootY, rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 1, rootY + 2 };
                break;
            case E:
                tetromino = new int[] { rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 1, rootY, rootX + 2, rootY };
                break;
            case S:
                tetromino = new int[] { rootX, rootY, rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 1, rootY + 2 };
                break;
            case W:
                tetromino = new int[] { rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 1, rootY, rootX + 2, rootY };
                break;
        }
    }

}
