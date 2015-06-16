package battle;

import battle.controllers.Dani.DaniController;
import battle.controllers.EmptyController;
import battle.controllers.FireForwardController;
import battle.controllers.Human.WASDController;
import battle.controllers.Naz.Naz_AI;
import battle.controllers.RotateAndShoot;
import battle.controllers.mmmcts.MMMCTS;
import battle.controllers.webpigeon.StaticEvolver;
import battle.controllers.webpigeon.StupidGAWrapper;
import math.Vector2d;

/**
 * Created by simon lucas on 10/06/15.
 */
public class BattleTest {
    BattleView view;

    public static void main(String[] args) {

        // Max speed: 5, Min: 1
        SimpleBattle battle = new SimpleBattle(true, 100, 3, 1, 10);

        BattleController player1 = new WASDController();
        BattleController player2 = new MMMCTS();
        battle.playGame(player1, player2);

        Vector2d s1Dist = battle.getShip(0).getTotalDistance();
        Vector2d s2Dist = battle.getShip(1).getTotalDistance();

        System.out.println("Distance: "+s1Dist+" vs "+s2Dist);
    }

}
