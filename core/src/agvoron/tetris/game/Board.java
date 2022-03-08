package agvoron.tetris.game;

import com.badlogic.gdx.utils.Array;

public class Board {

    private Array<Array<Square>> board;
    private int width;
    private int height;

    public Board() {
        this(10, 20);
    }

    public Board(int width, int height) {
        board = new Array<Array<Square>>();
        for (int i = 0; i < width; i++) {
            board.add(new Array<Square>());
            for (int j = 0; j < height; j++) {
                board.get(i).add(new Square());
            }
            board.get(i).shrink();
        }
        board.shrink();

        this.width = width;
        this.height = height;
    }

    public Array<Array<Square>> getGrid() {
        return board;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
