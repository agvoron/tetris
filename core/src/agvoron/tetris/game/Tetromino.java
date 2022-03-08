package agvoron.tetris.game;

public class Tetromino {

    public Shape shape;
    public Rotation rotation;

    public enum Shape {
        I, O, T, J, L, S, Z
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

    // root points to bottom left corner of shape
    public int[] getCoordinates(int rootX, int rootY) {
        switch (shape) {
            case I:
                return new int[] { rootX, rootY, rootX + 1, rootY, rootX + 2, rootY, rootX + 3, rootY };
            case J:
                return new int[] { rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 2, rootY + 1, rootX + 2, rootY };
            case L:
                return new int[] { rootX, rootY, rootX, rootY + 1, rootX + 1, rootY + 1, rootX + 2, rootY + 1 };
            case O:
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

    public void rotateRight() {
        rotation = rotation.right();
    }

    public void rotateLeft() {
        rotation = rotation.left();
    }

}
