package battle.controllers.Memo;

import asteroids.Action;
import asteroids.Controller;
import asteroids.GameState;
import asteroids.Ship;
import battle.BattleController;
import battle.NeuroShip;
import battle.RenderableBattleController;
import battle.SimpleBattle;
import math.Vector2d;
import battle.controllers.Memo.MemoControllerUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by Memo Akten on 11/06/15.
 */
public class MemoController1 implements RenderableBattleController {
    public double DESIRED_DIST_TO_ENEMY_MIN = 50;
    public double DESIRED_DIST_TO_ENEMY_MAX = 200;
    public double DESIRED_ROT_OFF_ENEMY = 30 * Math.PI / 180.0; // how many radians variance (+-) in axis off enemies back
    public double DESIRED_POS_CHANGE_PROB = 0.005;  //probability of picking new position to goto
    public double DIST_TO_TARGET_THRESH = 80;   // threshold for when we've reached our desired pos
    public double ROT_TO_TARGET_THRESH = 5 * Math.PI / 180.0;   // threshold for when we're aiming at desired pos

    public double ATTACK_PROB = 0.2;
    public double ATTACK_SHOOT_PROB = 0.5;
    public double ATTACK_THRUST_PROB = 0.00;
    public double ATTACK_ROT_THRESH = 5 * Math.PI / 180.0;

    public double CHASE_SHOOT_PROB = 0.1;
    public double CHASE_THRUST_PROB = 0.3;
    public double CHASE_DOT_THRESH = 0.9;

    public double MISSILE_AVOID_DIST = 100;
    public double MISSILE_AVOID_PROB = 0.5;

    Action action;
    double desired_dist_to_enemy = 0;
    double desired_rot_off_enemy = 0;
    Vector2d desired_pos = new Vector2d(true);
    Vector2d vec_to_desired_pos = new Vector2d(true);
    Vector2d target_pos = new Vector2d(true);


    public MemoController1() {
        action = new Action();
    }

    @Override
    public Action getAction(SimpleBattle gs, int playerId) {
        NeuroShip thisShip = gs.getShip(playerId);
        NeuroShip otherShip = gs.getShip(1 - playerId);

        boolean do_attack = false;

        // so they are rendered offscreen
        target_pos.set(-100, 100);
        desired_pos.set(-100, 100);

        // random probability of attacking
        if(Math.random() < ATTACK_PROB) {
            do_attack = true;

        } else {
            // pick desired distance to ship
            if(desired_dist_to_enemy == 0 || Math.random() < DESIRED_POS_CHANGE_PROB) {
                desired_dist_to_enemy = Math.random() * (DESIRED_DIST_TO_ENEMY_MAX - DESIRED_DIST_TO_ENEMY_MIN) + DESIRED_DIST_TO_ENEMY_MIN;
                desired_rot_off_enemy = (Math.random() - 0.5) * DESIRED_ROT_OFF_ENEMY * 2;
            }

            // set desired position behind enemy ship
            Vector2d desired_pos_offset = Vector2d.multiply(otherShip.d, desired_dist_to_enemy);
            desired_pos_offset.rotate(desired_rot_off_enemy);

            desired_pos = Vector2d.subtract(otherShip.s, desired_pos_offset);
            vec_to_desired_pos = Vector2d.subtract(desired_pos, thisShip.s);

            // goto desired pos
            boolean reached_desired_pos = MemoControllerUtils.thrustTo(thisShip.s, thisShip.d, desired_pos, DIST_TO_TARGET_THRESH, ROT_TO_TARGET_THRESH, action);

            // attack if we're there
            if(reached_desired_pos) {
                do_attack = true;
            } else {
                // otherwise thrust (random)
                if(Vector2d.scalarProduct(thisShip.d, Vector2d.normalise(vec_to_desired_pos)) > CHASE_DOT_THRESH) {
                    action.thrust = Math.random() < CHASE_THRUST_PROB ? 1 : 0;
                }
            }
        }

        if(do_attack) {
            action.thrust = Math.random() < ATTACK_THRUST_PROB ? 1 : 0;

            // TODO, include velocity vector in target pos
            target_pos = new Vector2d(otherShip.s, true);
            action.turn = MemoControllerUtils.lookAt(thisShip.s, thisShip.d, otherShip.s, ATTACK_ROT_THRESH);
            action.shoot = action.turn == 0 ? Math.random() < ATTACK_SHOOT_PROB : false;
        }



        return action;
    }

    @Override
    public void render(Graphics2D g, NeuroShip s) {
        int desired_pos_radius = (int)DIST_TO_TARGET_THRESH;
        g.setColor(Color.yellow);
        g.drawOval((int)(desired_pos.x) - desired_pos_radius, (int)(desired_pos.y) - desired_pos_radius, desired_pos_radius * 2, desired_pos_radius * 2);

       // System.out.println(desired_pos);


        g.setColor(Color.red);
        int target_pos_radius = 10;
        g.drawOval((int)(target_pos.x) - target_pos_radius, (int)(target_pos.y) - target_pos_radius, target_pos_radius * 2, target_pos_radius * 2);

        /*
        g.drawO
        color = playerID == 0 ? Color.green : Color.blue;
        AffineTransform at = g.getTransform();
        g.translate(s.x, s.y);
        double rot = Math.atan2(d.y, d.x) + Math.PI / 2;
        g.rotate(rot);
        g.scale(scale, scale);
        g.setColor(color);
        g.fillPolygon(xp, yp, xp.length);
        if (thrusting) {
            g.setColor(Color.red);
            g.fillPolygon(xpThrust, ypThrust, xpThrust.length);
        }
        g.setTransform(at);
        */
    }
}
