package agvoron.tetris.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class Tetromino {

    public Shape shape;
    public Rotation rotation;

    private static final Color LIGHT_BLUE = new Color(0.3f, 1f, 1f, 1f);
    private static final Color YELLOW = new Color(1f, 0.9f, 0.25f, 1f);
    private static final Color PURPLE = new Color(1f, 0.2f, 0.9f, 1f);
    private static final Color BLUE = new Color(0.2f, 0.2f, 1f, 1f);
    private static final Color ORANGE = new Color(1f, 0.6f, 0.2f, 1f);
    private static final Color GREEN = new Color(0.2f, 0.8f, 0.15f, 1f);
    private static final Color RED = new Color(0.9f, 0.15f, 0.15f, 1f);

    public enum Shape {
        I(LIGHT_BLUE), O(YELLOW), T(PURPLE), J(BLUE), L(ORANGE), S(GREEN), Z(RED);

        private final Color shapeColor;

        private Shape(Color shapeColor) {
            this.shapeColor = shapeColor;
        }

        public Color getColor() {
            return shapeColor;
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

    public Tetromino(Shape shape) {
        this.shape = shape;
        rotation = Rotation.N;
    }

    public Color getColor() {
        return shape.getColor();
    }

    // root points to bottom left corner of shape
    public int[] getCoordinates(int rootX, int rootY) {
        switch (shape) {
            case I:
                return coordsI(rootX, rootY);
            case J:
                return coordsJ(rootX, rootY);
            case L:
                return new int[] { rootX, rootY, rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 2, rootY + 1 };
            case O:
                // no rotation
                return new int[] { rootX, rootY, rootX + 1, rootY, rootX, rootY + 1, rootX + 1, rootY + 1 };
            case S:
                return new int[] { rootX, rootY, rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 2, rootY + 1 };
            case T:
                return new int[] { rootX, rootY + 1, rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 2, rootY + 1 };
            case Z:
                return new int[] { rootX, rootY + 1, rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 2, rootY };
            default:
                return null;
        }
    }

    // rotation info for I
    private int[] coordsI(int rootX, int rootY) {
        switch (rotation) {
            case N:
                return new int[] { rootX, rootY, rootX + 1, rootY, rootX + 2, rootY, rootX + 3, rootY };
            case E:
                return new int[] { rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 1, rootY + 2, rootX + 1, rootY + 3 };
            case S:
                // duplicate N
                return new int[] { rootX, rootY, rootX + 1, rootY, rootX + 2, rootY, rootX + 3, rootY };
            case W:
                // duplicate E
                return new int[] { rootX + 1, rootY, rootX + 1, rootY + 1, rootX + 1, rootY + 2, rootX + 1, rootY + 3 };
            default:
                return null;
        }
    }

    // rotation info for J
    private int[] coordsJ(int rootX, int rootY) {
        switch (rotation) {
            case N:
                return new int[] { rootX, rootY, rootX, rootY - 1, rootX, rootY + 1, rootX - 1, rootY - 1 };
            case E:
                return new int[] { rootX, rootY + 1, rootX, rootY, rootX + 1, rootY, rootX + 2, rootY };
            case S:
                return new int[] { rootX, rootY, rootX, rootY - 1, rootX, rootY + 1, rootX + 1, rootY + 1 };
            case W:
                return new int[] { rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 2, rootY + 1, rootX + 2, rootY };
            default:
                return null;
        }
    }

    public void rotateRight() {
        rotation = rotation.right();
    }

    public void rotateLeft() {
        rotation = rotation.left();
    }

    public static Tetromino createRandomPiece() {
        return new Tetromino(Shape.values()[MathUtils.random(Shape.values().length - 1)]);
    }

}
