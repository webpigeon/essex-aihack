package battle;

import sun.java2d.pipe.SpanShapeRenderer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by jwalto on 17/06/2015.
 */
public class GameStarter implements KeyListener {
    private SimpleBattle battle;

    public GameStarter(SimpleBattle simpleBattle) {
        this.battle = simpleBattle;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            if (battle.hasNotStarted()) {
                battle.startGame();
            } else if (battle.isGameOver()) {
                battle.quit();
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
