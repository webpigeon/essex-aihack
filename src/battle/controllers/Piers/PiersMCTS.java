package battle.controllers.Piers;

import asteroids.Action;
import battle.BattleController;
import battle.SimpleBattle;

/**
 * Created by pwillic on 11/06/2015.
 */
public class PiersMCTS implements BattleController {

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        MCTSNode root = new MCTSNode(2.0, playerId);

        return null;
    }
}
