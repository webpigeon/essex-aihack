package battle;

import javax.swing.*;

import asteroids.Action;
import battle.controllers.EmptyController;
import battle.controllers.FireController;
import battle.controllers.RotateAndShoot;
import math.Vector2d;
import utilities.JEasyFrame;

/**
 * Created by simon lucas on 10/06/15.
 */
public class BattleTest {
    BattleView view;

    public static void main(String[] args) {

        SimpleBattle battle = new SimpleBattle();

        BattleController fire = new RotateAndShoot();
        battle.playGame(fire, fire);
    }

}
