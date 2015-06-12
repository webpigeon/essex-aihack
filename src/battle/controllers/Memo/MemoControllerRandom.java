package battle.controllers.Memo;

import asteroids.Action;
import asteroids.Controller;
import asteroids.GameState;
import asteroids.Ship;
import battle.BattleController;
import battle.NeuroShip;
import battle.SimpleBattle;
import math.Vector2d;
import battle.controllers.Memo.MemoControllerUtils;

/**
 * Created by Memo Akten on 11/06/15.
 */
public class MemoControllerRandom implements BattleController {
    public double ATTACK_PROB = 0.1;
    public double ATTACK_SHOOT_PROB = 1;
    public double ATTACK_THRUST_PROB = 0.01;
    public double ATTACK_ROT_THRESH = 0.01;
    public double FLEE_ROT_CHANGE_PROB = 0.3;
    public double FLEE_SHOOT_PROB = 0.1;
    public double FLEE_THRUST_PROB = 0.8;
    public double FLEE_TURN_PROB = 0.3;
    public double FLEE_TURN_LEFT_PROB = 0.3333;
    public double FLEE_TURN_RIGHT_PROB = 0.3333;
    Action action;

    public MemoControllerRandom() {
        action = new Action();
    }

    @Override
    public Action getAction(SimpleBattle gs, int playerId) {
        NeuroShip thisShip = gs.getShip(playerId);
        NeuroShip otherShip = gs.getShip(1 - playerId);

        if(Math.random() < ATTACK_PROB) {
            action.thrust = Math.random() < ATTACK_THRUST_PROB ? 1 : 0;
            action.shoot = Math.random() < ATTACK_SHOOT_PROB;

            // TODO, include velocity vector in target pos
            action.turn = MemoControllerUtils.lookAt(thisShip.s, thisShip.d, otherShip.s, ATTACK_ROT_THRESH);
        } else{
            action.thrust = Math.random() < FLEE_THRUST_PROB ? 1 : 0;
            action.shoot = Math.random() < FLEE_SHOOT_PROB;

            if (Math.random() < FLEE_TURN_PROB) {
                double v = Math.random();
                if (v < FLEE_TURN_LEFT_PROB) action.turn = -1;
                else if (v > 1 - FLEE_TURN_RIGHT_PROB) action.turn = 1;
                else action.turn = 0;
            }
        }

        return action;
    }
}
