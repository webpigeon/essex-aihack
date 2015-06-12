package battle.controllers.Memo;

import asteroids.*;
import battle.BattleController;
import battle.NeuroShip;
import battle.RenderableBattleController;
import battle.SimpleBattle;
import math.Vector2d;
import battle.controllers.Memo.MemoControllerUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Created by Memo Akten on 11/06/15.
 */
public class MemoController1 implements RenderableBattleController {
    public double DESIRED_DIST_TO_ENEMY_MIN = 100;
    public double DESIRED_DIST_TO_ENEMY_MAX = 300;
    public double DESIRED_ROT_OFF_ENEMY = 00 * Math.PI / 180.0; // how many radians variance (+-) in axis off enemies back
    public double DESIRED_POS_CHANGE_PROB = 0.005;  //probability of picking new position to goto
  //  public double DIST_TO_TARGET_THRESH = 80;   // threshold for when we've reached our desired pos
    public double ROT_TO_TARGET_THRESH = 45 * Math.PI / 180.0;   // threshold for when we're aiming at desired pos

    public double ATTACK_PROB = 0.1;
    public double ATTACK_SHOOT_PROB = 0.3;
    public double ATTACK_THRUST_PROB = 0.00;
    public double ATTACK_ROT_THRESH = 45 * Math.PI / 180.0;

    public double CHASE_SHOOT_PROB = 0.1;
    public double CHASE_THRUST_PROB = 0.5;
    public double CHASE_DOT_THRESH = 0.7;

    public double MISSILE_AVOID_DIST = 80;
    public double MISSILE_AVOID_PROB = 0.2;

    public double SHOOT_DIST_THRESH = 1000;
    public int TICK_STEP = 1;

    Action action;
    double desired_dist_to_enemy = 0;
    double desired_rot_off_enemy = 0;
    double desired_pos_radius = 0;
    Vector2d desired_pos = new Vector2d(true);
    Vector2d vec_to_desired_pos = new Vector2d(true);
    Vector2d target_pos = new Vector2d(true);

    boolean do_attack = false;  // turn and attack
    boolean do_chase = false;   // go behind
    boolean do_avoid = false;   // avoid missiles
    boolean do_chicken = false; // go to edge and hide

    public MemoController1() {
        action = new Action();
    }

    @Override
    public Action getAction(SimpleBattle gs, int playerId) {
        if(TICK_STEP < 1) TICK_STEP = 1;
        if(TICK_STEP > 1) {
            if(gs.getTicks() % TICK_STEP != 0) return action;
        }

        NeuroShip thisShip = gs.getShip(playerId);
        NeuroShip otherShip = gs.getShip(1 - playerId);

        do_attack = false;
        do_chase = false;
        do_avoid = false;
        do_chicken = false;

        // random probability of attacking
        if(Math.random() < ATTACK_PROB) {
            do_attack = true;

        } else {
            do_chase = true;
            // pick desired distance to ship
            if(desired_dist_to_enemy == 0 || Math.random() < DESIRED_POS_CHANGE_PROB) {
                desired_dist_to_enemy = Math.random() * (DESIRED_DIST_TO_ENEMY_MAX - DESIRED_DIST_TO_ENEMY_MIN) + DESIRED_DIST_TO_ENEMY_MIN;
                desired_pos_radius = desired_dist_to_enemy;
                desired_rot_off_enemy = (Math.random() - 0.5) * DESIRED_ROT_OFF_ENEMY * 2;
            }

            // set desired position behind enemy ship
            Vector2d desired_pos_offset = Vector2d.multiply(otherShip.d, desired_dist_to_enemy);
            desired_pos_offset.rotate(desired_rot_off_enemy);

            desired_pos = Vector2d.subtract(otherShip.s, desired_pos_offset);
            vec_to_desired_pos = Vector2d.subtract(desired_pos, thisShip.s);

            // goto desired pos
            boolean reached_desired_pos = MemoControllerUtils.thrustTo(thisShip.s, thisShip.d, desired_pos, desired_pos_radius, ROT_TO_TARGET_THRESH, action);

            // attack if we're there
            if(reached_desired_pos) {
                do_attack = true;
            } else {
                // otherwise thrust (random)
                if(Vector2d.dot(thisShip.d, Vector2d.normalise(vec_to_desired_pos)) > CHASE_DOT_THRESH) {
                    action.thrust = Math.random() < CHASE_THRUST_PROB ? 1 : 0;
                }
            }
        }

        if(do_attack) {
            action.thrust = Math.random() < ATTACK_THRUST_PROB ? 1 : 0;

            // TODO, include velocity vector in target pos
            target_pos = new Vector2d(otherShip.s, true);
            action.shoot = MemoControllerUtils.lookAt(thisShip.s, thisShip.d, otherShip.s, ATTACK_ROT_THRESH, action) && (Math.random() < ATTACK_SHOOT_PROB);
        }

        if(thisShip.s.dist(otherShip.s) > SHOOT_DIST_THRESH) {
            action.shoot = false;
        }

        do_chicken = gs.getMissilesLeft(playerId) == 0;
        if(do_chicken) {
            MemoControllerUtils.thrustTo(thisShip.s, thisShip.d, new Vector2d(0, 0), desired_pos_radius, ROT_TO_TARGET_THRESH, action);
            action.thrust = 1;
        }

//        action.thrust = 0;

        if(Math.random() < MISSILE_AVOID_PROB) {
           // ArrayList<Missile> missiles = new ArrayList<Missile>();
            Vector2d avg_missile_pos = new Vector2d(true);
            int num_missiles = 0;

            // look for missiles nearby
            for (GameObject go : gs.getObjects()) {
                if (go instanceof Missile) {
                    if (go.s.dist(thisShip.s) < MISSILE_AVOID_DIST) {
                        //missiles.add((Missile) go);
                        num_missiles++;
                        avg_missile_pos.add(go.s);
                    }
                }
            }


            if(num_missiles > 0) {
                avg_missile_pos.multiply(1.0/num_missiles);
                do_avoid = true;
                Vector2d vec_to_avg_missile_pos = Vector2d.subtract(avg_missile_pos, thisShip.s);
                action.turn = Vector2d.crossMag(vec_to_avg_missile_pos, thisShip.d) < 0 ? - 1 : 1;
                action.thrust = 1;
            }
            //for(Missile m : missiles) {

            //}
        }



        return action;
    }

    @Override
    public void render(Graphics2D g, NeuroShip s) {
        //int desired_pos_radius = (int)DIST_TO_TARGET_THRESH;
        if(do_chase) {
            g.setColor(Color.yellow);
            g.drawOval((int) (desired_pos.x - desired_pos_radius), (int) (desired_pos.y - desired_pos_radius), (int) desired_pos_radius * 2, (int) desired_pos_radius * 2);
        }
       // System.out.println(desired_pos);

        if(do_attack) {
            g.setColor(Color.red);
            int r = 20;
            g.drawOval((int) (target_pos.x) - r, (int) (target_pos.y) - r, r * 2, r * 2);
        }

        if(do_avoid) {
            g.setColor(Color.green);
            int r = (int)MISSILE_AVOID_DIST;
            g.drawOval((int) (s.s.x) - r, (int) (s.s.y) - r, r * 2, r * 2);
        }

        if(do_chicken) {
            g.setColor(Color.pink);
            int r = 50;
            g.drawOval((int) (s.s.x) - r, (int) (s.s.y) - r, r * 2, r * 2);
        }

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
