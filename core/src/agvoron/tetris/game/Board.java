package agvoron.tetris.game;

import com.badlogic.gdx.utils.Array;

public class Board {

    /** Default width in number of tiles */
    private static final int BOARD_WIDTH = 10;
    /** Default height in number of tiles */
    private static final int BOARD_HEIGHT = 20;

    private Array<Array<Square>> board;

    public Board() {
        this(BOARD_WIDTH, BOARD_HEIGHT);
    }

    public Board(int width, int height) {
        board = new Array<Array<Square>>();
        for (int i = 0; i < height; i++) {
            board.add(new Array<Square>());
            for (int j = 0; j < width; j++) {
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
        return board.get(0).size;
    }

    public int getHeight() {
        return board.size;
    }

    public Square getSquare(int x, int y) {
        return board.get(y).get(x);
    }

    /**
     * Clear lines in board, give score and track combo on static score class as a side effect.
     * 
     * @return the number of lines cleared
     */
    public int clearLines() {
        int linesCleared = 0;
        for (int i = board.size - 1; i >= 0; i--) {
            boolean lineClear = true;
            for (int j = 0; j < board.get(i).size; j++) {
                if (!board.get(i).get(j).occupied) {
                    lineClear = false;
                    break;
                }
            }
            if (lineClear) {
                board.removeIndex(i);
                board.add(new Array<Square>());
                for (int j = 0; j < board.get(0).size; j++) {
                    board.get(board.size - 1).add(new Square());
                }
                linesCleared += 1;
            }
        }

        if (linesCleared > 0) {
            Score.addCombo();
            Score.clearLines(linesCleared);
        } else {
            Score.clearCombo();
        }

        return linesCleared;
    }

}
