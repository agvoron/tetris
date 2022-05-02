package agvoron.tetris.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Test;

import agvoron.tetris.game.Board;
import agvoron.tetris.game.Score;
import agvoron.tetris.game.Tetromino;
import agvoron.tetris.ui.TetrisScreen;

public class TetrisTests {

    public TetrisTests() {
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testDemoJUnit() {
        assertTrue(true);
    }

    @Test
    public void testDemoTetrisCore() {
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
    public void testDemoLibgdxFeatures() {
        Tetromino test = new Tetromino(new Board());
        assertEquals(test.getShape().getColor(), test.getColor());
    }

    @Test
    public void testDemoMockTetrisFeatures() {
        TetrisScreen mockTetrisScreen = mock(TetrisScreen.class);

        mockTetrisScreen.render(10);
        assertNotNull(mockTetrisScreen.toString());

        verify(mockTetrisScreen).render(10);
    }

    @Test
    public void testDemoMockGLFeatures() {
        // TODO replicate from the github repo I bookmarked
    }

}
