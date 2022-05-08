package agvoron.tetris.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Test;

import agvoron.tetris.Settings;
import agvoron.tetris.Tetris;
import agvoron.tetris.game.TetrisController;
import agvoron.tetris.ui.TetrisScreen;

/**
 * Oh my gosh! I can finally test stuff for real!
 */
public class ControllerTests {

    private TetrisScreen mockTetrisScreen;
    private TetrisController ctrlr;

    public ControllerTests() {
        Tetris.settings = new Settings(); // static state for test environment
        mockTetrisScreen = mock(TetrisScreen.class);
        ctrlr = new TetrisController(mockTetrisScreen);
    }

    @Test
    public void testInitializiation() {
        assertNotNull(ctrlr.getBoard());
        assertNotNull(ctrlr.getCurrPiece());
        assertFalse(ctrlr.getCurrPiece().fall());
        assertFalse(ctrlr.isGamePaused());
        assertFalse(ctrlr.isGameLost());
    }

    @Test
    public void testTetrominoFalling() {
        while (!ctrlr.getCurrPiece().fall()) {
        }
        assertTrue(ctrlr.getCurrPiece().fall());
        ctrlr.resetGame();
        assertFalse(ctrlr.getCurrPiece().fall());
        ctrlr.resetGame();
    }

    @Test
    public void testPausingAndLosingStates() {
        ctrlr.pauseGame();
        assertTrue(ctrlr.isGamePaused());
        ctrlr.unpauseGame();
        assertFalse(ctrlr.isGamePaused());
        ctrlr.pauseGame();
        ctrlr.resetGame();
        assertFalse(ctrlr.isGamePaused());
        ctrlr.loseGame();
        assertTrue(ctrlr.isGameLost());
        ctrlr.resetGame();
        assertFalse(ctrlr.isGameLost());
    }

    // TODO think about a test like this - should be able to mock it up
    // TODO or think about conclusions for separating input processing from
    // controller
//    @Test
//    public void testBasicGameLoop() {
//        Tetromino testCurr = ctrlr.getCurrPiece();
//        // 9 sec, it takes 11 seconds for the first piece to place
//        for (int i=0; i<90; i++) {
//            ctrlr.gameLoopUpdate(0.1f);
//        }
//        assertTrue(testCurr == ctrlr.getCurrPiece());
//        for (int i=0; i<30; i++) {
//            ctrlr.gameLoopUpdate(0.1f);
//        }
//        // piece should have placed by now
//        assertFalse(testCurr == ctrlr.getCurrPiece());
//    }

}
