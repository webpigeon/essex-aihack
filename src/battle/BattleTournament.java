package battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jwalto on 11/06/2015.
 */
public class BattleTournament {
    private List<BattleController> controllers;
    private SimpleBattle battleEngine;
    private Map<BattleController, >

    public BattleTournament() {
        this.controllers = new ArrayList<>();
        this.battleEngine = new SimpleBattle();
    }

    public void runGame(BattleController player1, BattleController player2) {

        battleEngine.playGame(player1, player2);
        List<SimpleBattle.PlayerStats> stats = battleEngine.stats;



    }

    private static class BattleStats {
    }
}
