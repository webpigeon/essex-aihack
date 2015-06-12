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
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        MCTSNode root = new MCTSNode(2.0, playerId);
        GameTimer timer = new GameTimer();
        timer.setTimeBudgetMilliseconds(40);
        int i = 0;
        while (timer.remainingTimePercent() > 10) {
//            System.out.println("Running");
            SimpleBattle copy = gameStateCopy.clone();
            MCTSNode travel = root.select(copy, 3);
            double[] results = travel.rollout(copy, 100);
//            System.out.println(Arrays.toString(results));
            travel.updateValues(results[0], results[1]);
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
