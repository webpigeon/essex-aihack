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

        NeuroShip s1 = buildShip(250,250);
        NeuroShip s2 = buildShip(300,300);

        SimpleBattle battle = new SimpleBattle(s1, s2);

        BattleController fire = new RotateAndShoot();
        battle.playGame(fire, fire);
    }

    public static NeuroShip buildShip(int x, int y) {
        Vector2d position = new Vector2d(x, y);
        Vector2d speed = new Vector2d();
        Vector2d direction = new Vector2d(1, 0);

        NeuroShip ship = new NeuroShip(position, speed, direction );
        return ship;
    }

}
