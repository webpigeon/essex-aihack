package battle.controllers.Piers;

import asteroids.Action;
import battle.BattleController;
import battle.SimpleBattle;

/**
 * Created by pwillic on 11/06/2015.
 */
public class PiersMCTS implements BattleController {

    public PiersMCTS() {
        MCTSNode.setAllActions();
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        MCTSNode root = new MCTSNode(2.0, playerId);
        GameTimer timer = new GameTimer();
        timer.setTimeBudgetMilliseconds(40);
        while (timer.remainingTimePercent() > 10) {
            MCTSNode travel = root.select(gameStateCopy, 3);
            double[] results = travel.rollout(gameStateCopy);
            travel.updateValues(results[0], results[1]);
        }

        return root.getBestAction();
    }
}
