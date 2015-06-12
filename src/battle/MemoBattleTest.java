package battle;

import battle.controllers.Memo.MemoControllerRandom;
import battle.controllers.Memo.MemoController1;

/**
 * Created by simon lucas on 10/06/15.
 */
public class MemoBattleTest {
    BattleView view;

    public static void main(String[] args) {

        SimpleBattle battle = new SimpleBattle();

        BattleController player1 = new MemoController1();
        BattleController player2 = new MemoControllerRandom();
        battle.playGame(player1, player2);
    }

}
