package battle;

import javax.swing.*;

import asteroids.Action;
import battle.controllers.EmptyController;
import battle.controllers.FireController;
import battle.controllers.RotateAndShoot;
import battle.controllers.DaniController;
import math.Vector2d;
import utilities.JEasyFrame;

/**
 * Created by simon lucas on 10/06/15.
 */
public class DaniBattleTest {
    BattleView view;

    public static void main(String[] args) {

        SimpleBattle battle = new SimpleBattle();

        BattleController fire1 = new DaniController();
        BattleController fire2 = new RotateAndShoot();

        battle.playGame(fire1, fire2);
    }

}
