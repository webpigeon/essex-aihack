package battle;

import battle.controllers.Dani.DaniController;
import battle.controllers.FireForwardController;
import battle.controllers.Human.WASDController;
import battle.controllers.Memo.MemoController1;
import battle.controllers.Memo.MemoControllerRandom;
import battle.controllers.Naz.Naz_AI;
import battle.controllers.RotateAndShoot;
import battle.controllers.mmmcts.MMMCTS;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by jwalto on 16/06/2015.
 */
public class PlayerExperiment {

    public static void main(String[] args) throws FileNotFoundException {
        BattleTournament bt = new BattleTournament(true, 100, 3, 1, 10);
        bt.addController(new WASDController());
        bt.addController(new MMMCTS());

        bt.runMatchups();
    }


}
