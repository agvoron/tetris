package agvoron.tetris.game;

import com.badlogic.gdx.utils.Array;

public class Board {

    // default dimensions in number of tiles
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    private Array<Array<Square>> board;

    public Board() {
        this(BOARD_WIDTH, BOARD_HEIGHT);
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
    }

    public Array<Array<Square>> getGrid() {
        return board;
    }

    public int getWidth() {
        return board.size;
    }

    public int getHeight() {
        return board.get(0).size;
    }

    public Square getSquare(int x, int y) {
        return board.get(x).get(y);
    }

}
