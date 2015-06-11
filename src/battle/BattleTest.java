package battle;

import javax.swing.*;
import math.Vector2d;

/**
 * Created by simon lucas on 10/06/15.
 */
public class BattleTest {

    public static void main(String[] args) {

        NeuroShip s1 = buildShip(0,0);
        NeuroShip s2 = buildShip(0,0);

        SimpleBattle battle = new SimpleBattle(s1, s2);
        BattleView view = new BattleView(battle);

        JFrame frame = new JFrame();
        frame.add(view);
        frame.pack();
        frame.setVisible(true);


    }

    public static NeuroShip buildShip(int x, int y) {
        Vector2d position = new Vector2d();
        Vector2d speed = new Vector2d();
        Vector2d direction = new Vector2d();

        NeuroShip ship = new NeuroShip(null, position, speed, direction );
        return ship;
    }

}
