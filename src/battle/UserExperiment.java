package battle;

import asteroids.Game;
import battle.controllers.Dani.DaniController;
import battle.controllers.FireForwardController;
import battle.controllers.Human.MemoHuman;
import battle.controllers.Human.WASDController;
import battle.controllers.Memo.MemoController1;
import battle.controllers.Memo.MemoControllerRandom;
import battle.controllers.Naz.Naz_AI;
import battle.controllers.Piers.PiersMCTS;
import battle.controllers.RotateAndShoot;
import battle.controllers.mmmcts.MMMCTS;
import sun.java2d.pipe.SpanShapeRenderer;
import utilities.JEasyFrame;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jwalto on 11/06/2015.
 */
public class UserExperiment {
    private final static Integer NUM_ROUNDS = 1;

    private GenerateCSV summary;
    private GenerateCSV detail;
    private Map<BattleController, BattleStats> scores;

    private List<SimpleBattle> games;
    private BattleController p1;
    private BattleController p2;

    public UserExperiment(BattleController p1, BattleController p2) throws FileNotFoundException
    {
        this.games = new ArrayList<SimpleBattle>();
        this.p1 = p1;
        this.p2 = p2;

        this.scores = new HashMap<>();

        long partipantID = System.currentTimeMillis();
        this.detail = new GenerateCSV(String.format("detail-human[%d].csv", partipantID ));
        this.summary = new GenerateCSV(String.format("summary-human[%d].csv", partipantID ));

        summary.writeLine("class", "wins", "losses", "draws");
        detail.writeLine("run number",
                "player1",
                "player2",
                "s1Score",
                "s2Score",
                "s1Missles",
                "s2Missles",
                "gameTicks",
                "s1DistX",
                "s1DistY",
                "s2DistX",
                "s2DistY",
                "s1Mag",
                "s2Mag",
                "s1Wraps",
                "s2Wraps",
                "numBullets",
                "topSpeed",
                "acceleration",
                "rotationDegreesPerTick",
                "code"
                );
        //stuff to store
        // totalMagMoved
    }

    public void addGame(SimpleBattle game) {
        this.games.add(game);
    }

    public Map<BattleController, BattleStats> getScores() {
        return Collections.unmodifiableMap(scores);
    }

    public void runMatchups() {
        System.out.println(games.size() + " games");

        List<SimpleBattle> gameRandomised = new ArrayList<SimpleBattle>(games);
        Collections.shuffle(gameRandomised);

        System.out.println(gameRandomised);
        for (SimpleBattle game : gameRandomised) {
            System.out.println("starting game "+game);
            runRounds(game, NUM_ROUNDS);
        }

        for (Map.Entry<BattleController, BattleStats>  bs : scores.entrySet()) {
            BattleController controller = bs.getKey();
            BattleStats stats = bs.getValue();

            summary.writeLine(controller.getClass().getSimpleName(), stats.wins, stats.losses, stats.draws);
        }

        detail.close();
        summary.close();
    }

    public void runRounds(SimpleBattle game, int rounds) {
        for (int i=0; i<rounds; i++) {
            runGame(game, i);
        }
    }

    public void runGame(SimpleBattle game, int runID) {
        game.playGame(p1, p2);

        BattleStats p1Stats = scores.getOrDefault(p1, new BattleStats());
        BattleStats p2Stats = scores.getOrDefault(p2, new BattleStats());

        scores.put(p1, p1Stats);
        scores.put(p2, p2Stats);

        int p1Score = game.getPoints(0);
        int p2Score = game.getPoints(1);

        if (p1Score == p2Score) {
            int p1Bullets = game.getMissilesLeft(0);
            int p2Bullets = game.getMissilesLeft(1);
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

        NeuroShip ship1 = game.getShip(0);
        NeuroShip ship2 = game.getShip(1);

        // Player 1 view
        detail.writeLine("p1"+runID,
                p1.getClass(),
                p2.getClass(),
                p1Score/10,
                p2Score/10,
                game.getMissilesLeft(0),
                game.getMissilesLeft(1),
                game.getTicks(),
                ship1.getTotalDistance().x,
                ship1.getTotalDistance().y,
                ship2.getTotalDistance().x,
                ship2.getTotalDistance().y,
                ship1.totalMag,
                ship2.totalMag,
                game.getStats(0).getWraps(),
                game.getStats(1).getWraps(),
                game.nMissiles,
                game.topSpeed,
                game.acceleration,
                game.rotationDegreesPerTick
                );

        // Player 2 view
        detail.writeLine("p2"+runID,
                p2.getClass(),
                p1.getClass(),
                p2Score/10,
                p1Score/10,
                game.getMissilesLeft(1),
                game.getMissilesLeft(0),
                game.getTicks(),
                ship2.getTotalDistance().x,
                ship2.getTotalDistance().y,
                ship1.getTotalDistance().x,
                ship1.getTotalDistance().y,
                ship2.totalMag,
                ship1.totalMag,
                game.getStats(1).getWraps(),
                game.getStats(0).getWraps(),
                game.nMissiles,
                game.topSpeed,
                game.acceleration,
                game.rotationDegreesPerTick,
                game.code
        );
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

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        BattleController p1 = new MemoHuman();
        BattleController p2 = new MMMCTS();

        UserExperiment exp = new UserExperiment(p1, p2);
        exp.addGame(new SimpleBattle(true, 10, 3, 1, 10, 'A'));
        exp.addGame(new SimpleBattle(true, 100, 3, 1, 10, 'B'));
        exp.addGame(new SimpleBattle(true, 1000, 3, 1, 10, 'C'));

        exp.runMatchups();

        JFrame frame = SimpleBattle.frame;
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new JLabel("game over."));
        frame.revalidate();
        frame.pack();
        frame.repaint();
    }
}
