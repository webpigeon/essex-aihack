package battle;

import battle.controllers.EmptyController;
import battle.controllers.FireForwardController;
import battle.controllers.Human.WASDController;
import battle.controllers.Naz.Naz_AI;
import battle.controllers.RotateAndShoot;
import battle.controllers.webpigeon.StaticEvolver;
import battle.controllers.webpigeon.StupidGAWrapper;
import math.Vector2d;

/**
 * Created by simon lucas on 10/06/15.
 */
public class BattleTest {
    BattleView view;

    public static void main(String[] args) {

        SimpleBattle battle = new SimpleBattle();

        BattleController player1 = new WASDController();
        BattleController player2 = new RotateAndShoot();
        battle.playGame(player1, player2);

        Vector2d s1Dist = battle.getShip(0).getTotalDistance();
        Vector2d s2Dist = battle.getShip(1).getTotalDistance();

        System.out.println("Distance: "+s1Dist+" vs "+s2Dist);
    }

}
