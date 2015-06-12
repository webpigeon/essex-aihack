package battle.controllers.Piers;

import asteroids.Action;
import battle.BattleController;
import battle.SimpleBattle;

import java.util.Arrays;

/**
 * Created by pwillic on 11/06/2015.
 */
public class PiersMCTS implements BattleController {

    public PiersMCTS() {
        MCTSNode.setAllActions();
        BetterMCTSNode.setAllActions();
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        BetterMCTSNode root = new BetterMCTSNode(2.0, playerId);
        GameTimer timer = new GameTimer();
        timer.setTimeBudgetMilliseconds(40);
        int i = 0;
        while (timer.remainingTimePercent() > 10) {
            SimpleBattle copy = gameStateCopy.clone();
            BetterMCTSNode travel = root.select(copy, 5);
            double result = travel.rollout(copy, 50);
            travel.updateValues(result);
            i++;
        }

//        if(i % 100 == 0){
//            root.printAllChildren();
//        }
//        System.out.println("Rollouts achieved: " + i);
//        System.out.println("Best Action: " + root.getBestAction());
        return root.getBestAction();
    }
}
