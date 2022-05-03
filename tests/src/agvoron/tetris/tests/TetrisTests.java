package agvoron.tetris.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Test;

import agvoron.tetris.game.Board;
import agvoron.tetris.game.Score;
import agvoron.tetris.game.Tetromino;
import agvoron.tetris.game.Tetromino.Rotation;
import agvoron.tetris.game.Tetromino.Shape;
import agvoron.tetris.ui.TetrisScreen;

public class TetrisTests {

    public TetrisTests() {
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testScore() {
        assertEquals(Score.getScore(), 0);
        Score.addScore(100);
        assertEquals(Score.getScore(), 100);
        Score.reset();
        assertEquals(Score.getScore(), 0);
        Score.addCombo();
        Score.clearLines(1);
        assertEquals(Score.getScore(), 1000);
        Score.addCombo();
        Score.clearLines(1);
        assertTrue(Score.getScore() > 2000);
    }

    @Test
    public void testTetrominoFields() {
        Tetromino test = new Tetromino(new Board());
        assertEquals(test.getShape().getColor(), test.getColor());
    }
    
    @Test
    public void testTetrominoRotation() {
        Tetromino test = new Tetromino(new Board(), Shape.I);
        test.setRotation(Rotation.N);
        int[] tetromino = test.getTetromino();
        int y = tetromino[1];
        for (int i=1; i<tetromino.length; i += 2) {
            assertEquals(y, tetromino[i]);
        }
        test.rotateLeft();
        tetromino = test.getTetromino();
        int x = tetromino[0];
        for (int i=0; i<tetromino.length; i += 2) {
            assertEquals(x, tetromino[i]);
        }
        test.rotateFlip();
        tetromino = test.getTetromino();
        for (int i=0; i<tetromino.length; i += 2) {
            assertEquals(x, tetromino[i]);
        }
    }

    @Test
    public void testExampleMock() {
        TetrisScreen mockTetrisScreen = mock(TetrisScreen.class);
        mockTetrisScreen.render(10);
        assertNotNull(mockTetrisScreen.toString());
        verify(mockTetrisScreen).render(10);
    }

    @Test
    public void testExampleMockGLFeatures() {
        // TODO replicate from the github repo I bookmarked
        // https://github.com/TomGrill/gdx-testing/tree/master/tests/src/de/tomgrill/gdxtesting
        assertTrue(true);
    }

}
