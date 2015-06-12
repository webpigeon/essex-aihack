package battle;

import battle.controllers.EmptyController;
import battle.controllers.FireForwardController;
import battle.controllers.Memo.MemoController1;
import battle.controllers.Memo.MemoControllerRandom;
import battle.controllers.Piers.PiersMCTS;
import battle.controllers.RotateAndShoot;
import battle.controllers.mmmcts.MMMCTS;

import java.util.*;

/**
 * Created by jwalto on 11/06/2015.
 */
public class BattleTournament {
    private final static Integer NUM_ROUNDS = 5;

    private List<BattleController> controllers;
    private SimpleBattle battleEngine;
    private Map<BattleController, BattleStats> scores;

    public BattleTournament() {
        this.controllers = new ArrayList<>();
        this.battleEngine = new SimpleBattle(false);
        this.scores = new HashMap<>();
    }

    public void addController(BattleController controller) {
        this.controllers.add(controller);
    }

    public Map<BattleController, BattleStats> getScores() {
        return Collections.unmodifiableMap(scores);
    }

    public void runMatchups() {
        System.out.println(controllers.size() + " controllers");

        for (BattleController p1 : controllers) {
            for (BattleController p2 : controllers) {
                if (p1 != p2) {
                    System.out.println("running "+p1.getClass().getSimpleName()+" vs "+p2.getClass().getSimpleName());
                    runRounds(p1, p2, NUM_ROUNDS);
                }
            }
        }
    }

    public void runRounds(BattleController p1, BattleController p2, int rounds) {
        for (int i=0; i<rounds; i++) {
            runGame(p1, p2);
        }
    }

    public void runGame(BattleController player1, BattleController player2) {
        battleEngine.playGame(player1, player2);

        BattleStats p1Stats = scores.getOrDefault(player1, new BattleStats());
        BattleStats p2Stats = scores.getOrDefault(player2, new BattleStats());

        scores.put(player1, p1Stats);
        scores.put(player2, p2Stats);

        int p1Score = battleEngine.getPoints(0);
        int p2Score = battleEngine.getPoints(1);

        if (p1Score == p2Score) {
            int p1Bullets = battleEngine.getMissilesLeft(0);
            int p2Bullets = battleEngine.getMissilesLeft(1);
            if (p1Bullets == p2Bullets) {
                //uber draw
                p1Stats.draws++;
                p2Stats.draws++;
            } else {
                if (p1Bullets > p2Bullets) {
                    p1Stats.wins++;
                    p2Stats.losses++;
                } else {
                    p2Stats.wins++;
                    p1Stats.losses++;
                }
            }
        } else {
            if (p1Score > p2Score) {
                p1Stats.wins++;
                p2Stats.losses++;
            } else {
                p2Stats.wins++;
                p1Stats.losses++;
            }
        }

    }

    private static class BattleStats {
        int wins;
        int losses;
        int draws;

        public String toString(){
            String score = "wins: %4d, losses: %4d, draws: %4d";
            return String.format(score, wins, losses, draws);
        }
    }

    public static void main(String[] args) {
        BattleTournament bt = new BattleTournament();

        bt.addController(new MemoControllerRandom());
        bt.addController(new MemoController1());
        bt.addController(new MMMCTS());
        bt.addController(new PiersMCTS());

        // Dippy AIs
        bt.addController(new FireForwardController());
        bt.addController(new RotateAndShoot());

        bt.runMatchups();

        Map<BattleController, BattleStats> scores = bt.getScores();
        for (Map.Entry<BattleController, BattleStats>  bs : scores.entrySet()) {
            String formatStr = "%50s %s";
            System.out.println(String.format(formatStr, bs.getKey().getClass().getSimpleName(), bs.getValue()));
        }
    }
}
