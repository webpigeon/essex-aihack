package battle.controllers.webpigeon;

import asteroids.Action;
import battle.SimpleBattle;
import ga.Eval2;

/**
 * Created by jwalto on 12/06/2015.
 */
public class GameEvaluator implements Eval2 {
    private SimpleBattle battle;

    public GameEvaluator(SimpleBattle battle, boolean reset) {
        this.battle = battle.clone();

        if (reset) {
            this.battle.reset();
        }
    }

    @Override
    public double pointsDiff(double[] a, double[] b) {
        SimpleBattle currBattle = battle.clone();
        currBattle.reset();

        GaController controller1 = new GaController(a);
        GaController controller2 = new GaController(b);

        while(!battle.isGameOver()) {
            Action action1 = controller1.getMove();
            Action action2 = controller2.getMove();

            currBattle.update(action1, action2);
        }

        return 0;
    }

}
