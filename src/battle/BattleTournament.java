package battle;

import battle.controllers.Dani.DaniController;
import battle.controllers.EmptyController;
import battle.controllers.FireForwardController;
import battle.controllers.Memo.MemoController1;
import battle.controllers.Memo.MemoControllerRandom;
import battle.controllers.Naz.Naz_AI;
import battle.controllers.Piers.PiersMCTS;
import battle.controllers.RotateAndShoot;
import battle.controllers.mmmcts.MMMCTS;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jwalto on 11/06/2015.
 */
public class BattleTournament {
    private final static Integer NUM_ROUNDS = 3;

    private GenerateCSV summary;
    private GenerateCSV detail;
    private List<BattleController> controllers;
    private SimpleBattle battleEngine;
    private Map<BattleController, BattleStats> scores;

    public BattleTournament() throws FileNotFoundException
    {
        this(false, 100, 3.0, 1.0, 10);
    }

    public BattleTournament(boolean visible, int numBullets, double topSpeed, double acceleration, double rotationDegreesPerTick) throws FileNotFoundException
    {
        this.controllers = new ArrayList<>();
        this.battleEngine = new SimpleBattle(visible, numBullets, topSpeed, acceleration, rotationDegreesPerTick);
        this.scores = new HashMap<>();
        this.detail = new GenerateCSV(String.format("detail[%d,%.2f,%.2f,%.2f].csv", numBullets, topSpeed, acceleration, rotationDegreesPerTick ));
        this.summary = new GenerateCSV(String.format("summary[%d,%.2f,%.2f,%.2f].csv", numBullets, topSpeed, acceleration, rotationDegreesPerTick ));

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
                "rotationDegreesPerTick");
        //stuff to store
        // totalMagMoved
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

        for (Map.Entry<BattleController, BattleStats>  bs : scores.entrySet()) {
            BattleController controller = bs.getKey();
            BattleStats stats = bs.getValue();

            summary.writeLine(controller.getClass().getSimpleName(), stats.wins, stats.losses, stats.draws);
        }

        detail.close();
        summary.close();
    }

    public void runRounds(BattleController p1, BattleController p2, int rounds) {
        for (int i=0; i<rounds; i++) {
            runGame(p1, p2, i);
        }
    }

    public void runGame(BattleController player1, BattleController player2, int runID) {
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

        NeuroShip ship1 = battleEngine.getShip(0);
        NeuroShip ship2 = battleEngine.getShip(1);

        // Player 1 view
        detail.writeLine("p1"+runID,
                player1.getClass(),
                player2.getClass(),
                p1Score/10,
                p2Score/10,
                battleEngine.getMissilesLeft(0),
                battleEngine.getMissilesLeft(1),
                battleEngine.getTicks(),
                ship1.getTotalDistance().x,
                ship1.getTotalDistance().y,
                ship2.getTotalDistance().x,
                ship2.getTotalDistance().y,
                ship1.totalMag,
                ship2.totalMag,
                battleEngine.getStats(0).getWraps(),
                battleEngine.getStats(1).getWraps(),
                battleEngine.nMissiles,
                battleEngine.topSpeed,
                battleEngine.acceleration,
                battleEngine.rotationDegreesPerTick
                );

        // Player 2 view
        detail.writeLine("p2"+runID,
                player2.getClass(),
                player1.getClass(),
                p2Score/10,
                p1Score/10,
                battleEngine.getMissilesLeft(1),
                battleEngine.getMissilesLeft(0),
                battleEngine.getTicks(),
                ship2.getTotalDistance().x,
                ship2.getTotalDistance().y,
                ship1.getTotalDistance().x,
                ship1.getTotalDistance().y,
                ship2.totalMag,
                ship1.totalMag,
                battleEngine.getStats(1).getWraps(),
                battleEngine.getStats(0).getWraps(),
                battleEngine.nMissiles,
                battleEngine.topSpeed,
                battleEngine.acceleration,
                battleEngine.rotationDegreesPerTick
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

    private static Callable<Object> RunTournament(int numBullets, double topSpeed, double acceleration, double rotationDegreesPerTick) throws FileNotFoundException
    {
        Callable task = new Callable() {
            @Override
            public Object call() throws Exception {
                try {
                    BattleTournament bt = new BattleTournament(false, numBullets, topSpeed, acceleration, rotationDegreesPerTick);

                    //players
                    bt.addController(new MemoController1());
                    bt.addController(new MMMCTS());
                    //bt.addController(new PiersMCTS());
                    bt.addController(new Naz_AI());
                    bt.addController(new DaniController());

                    //extras
                    bt.addController(new MemoControllerRandom());

                    // Dippy AIs
                    bt.addController(new FireForwardController());
                    bt.addController(new RotateAndShoot());

                    bt.runMatchups();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };
        return task;
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        ExecutorService service = Executors.newFixedThreadPool(1);

        int defaultNumBullets = 100;
        double defaultTopSpeed = 3.0;
        double defaultAcceleration = 1.0;
        double defaultRotationDegreesPerTick = 10;

        int[] numBullets = new int[] { 10, 1000 };
        double[] topSpeeds = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0 };
        double[] accelerations = new double[] { 0.5, 1.0, 1.5, 2.0, 3.0 };
        double[] rotationDegreesPerTick = new double[] {1, 5, 10, 15, 25};

        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

        tasks.add(RunTournament(defaultNumBullets, defaultTopSpeed, defaultAcceleration, defaultRotationDegreesPerTick));

        for(int i = 0; i < numBullets.length; i++)
        {
            tasks.add(RunTournament(numBullets[i], defaultTopSpeed, defaultAcceleration, defaultRotationDegreesPerTick));
        }

        for(int i = 0; i < topSpeeds.length; i++)
        {
            tasks.add(RunTournament(defaultNumBullets, topSpeeds[i], defaultAcceleration, defaultRotationDegreesPerTick));
        }

        for(int i = 0; i < accelerations.length; i++)
        {
            tasks.add(RunTournament(defaultNumBullets, defaultTopSpeed, accelerations[i], defaultRotationDegreesPerTick));
        }

        for(int i = 0; i < rotationDegreesPerTick.length; i++)
        {
            tasks.add(RunTournament(defaultNumBullets, defaultTopSpeed, defaultAcceleration, rotationDegreesPerTick[i]));
        }

        service.invokeAll(tasks);
        service.shutdown();
    }
}
