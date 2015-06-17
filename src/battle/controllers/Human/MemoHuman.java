package battle.controllers.Human;

import asteroids.Action;
import battle.BattleController;
import battle.SimpleBattle;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by jwalto on 12/06/2015.
 */
public class MemoHuman implements BattleController, KeyListener {
    Action currentAction;

    public MemoHuman() {
        this.currentAction = new Action(0,0,false);
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        return currentAction;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                currentAction.thrust = 1;
                break;

            case KeyEvent.VK_A:
                currentAction.turn = -1;
                break;

            case KeyEvent.VK_D:
                currentAction.turn = 1;
                break;

            case KeyEvent.VK_SPACE:
                currentAction.shoot = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                currentAction.thrust = 0;
                break;

            case KeyEvent.VK_A:
                currentAction.turn = 0;
                break;

            case KeyEvent.VK_D:
                currentAction.turn = 0;
                break;

            case KeyEvent.VK_SPACE:
                currentAction.shoot = false;
                break;
        }
    }
}