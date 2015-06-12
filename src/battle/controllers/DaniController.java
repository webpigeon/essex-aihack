package battle.controllers;

import asteroids.Action;
import asteroids.Controller;
import asteroids.GameState;
import asteroids.GameObject;
import asteroids.Ship;
import battle.BattleController;
import battle.BattleMissile;
import battle.NeuroShip;
import battle.SimpleBattle;
import asteroids.Missile;

import java.awt.*;
import java.util.ArrayList;
import math.Vector2d;
import java.awt.geom.AffineTransform;

/**
 * Created by simonlucas on 30/05/15.
 */
public class DaniController implements BattleController
{

    Action action;
    Vector2d targetPos;
    double viewRadius = 20.0;
    double thrustAmt = 1.5;
    double rotAmt = 1.5;
    int shotWait = 0;
    Vector2d posSum;
    Vector2d dirSum;
    boolean anyMissiles = false;

    public DaniController()

    {
        action = new Action();
        targetPos = new Vector2d(0,0);
        thrustAmt = 0.5 + Math.random();
    }

    public Action action(GameState game) {
        // action.thrust = 2.0;
        action.shoot = true;
        action.turn = 1;

        return action;
    }

    public double angleBetween( Vector2d a, Vector2d b )
    {
        return Math.atan2(a.x * b.y - a.y * b.x, a.x * b.x + a.y * b.y);
    }

    public boolean inView( NeuroShip ship, NeuroShip enemy )
    {
        Vector2d enemyPos = new Vector2d(enemy.s);
        Vector2d thisPos = new Vector2d(ship.s);
        double l = enemyPos.dist(thisPos);
        Vector2d tp = new Vector2d();
        // Now tell me how to write this in one line in Java with vectors xcxicwerwx
        // Porco dio porca madonna tutti gli angeli in colonna
        Vector2d d = new Vector2d(ship.d);
        d.normalise();;
        tp.x = thisPos.x + d.x * l;
        tp.y = thisPos.y + d.y * l;
        targetPos = tp;

        if( tp.dist(enemyPos) <= viewRadius)
            return true;
        return false;
    }

    ArrayList<Missile> getMissiles(SimpleBattle gstate)
    {
        ArrayList<GameObject> O = gstate.getObjects();
        ArrayList<Missile> M = new ArrayList<Missile>();

        for( GameObject go : O )
        {
            if( go instanceof Missile )
            {
                M.add((Missile)go);
            }
        }

        return M;
    }

    double dot( Vector2d a, Vector2d b )
    {
        return a.x*b.x + a.y*b.y;
    }

    Vector2d subtract( Vector2d a, Vector2d b )
    {
        return new Vector2d(a.x-b.x, a.y-b.y);
    }

    Vector2d  closestPointOnSegment( Vector2d p, Vector2d a, Vector2d b )
    {
        Vector2d v = subtract(b,a);
        Vector2d w = subtract(p,a);


        double d1 = dot(w,v);
        if( d1 <= 0.0 )
            return a;
        double d2 = dot(v,v);
        if( d2 <= d1 )
            return b;

        double t = d1/d2;
        return a + v.mul(t);
    }

    double           distanceToSegment( Vector2d p, Vector2d a, Vector2d b )
    {
        return p.dist( closestPointOnSegment(p,a,b) );
    }


    Vector2d getPerp( Vector2d v )
    {
        return new Vector2d(-v.y, v.x);
    }

    @Override
    public Action getAction(SimpleBattle gstate, int playerId)
    {
        Action res = new Action(0,0,false);
        NeuroShip ship = gstate.getShip(playerId);
        NeuroShip enemy = gstate.getShip((playerId == 1)?0:1);

        Vector2d enemyPos = enemy.s;
        Vector2d thisPos = ship.s;
        Vector2d d = new Vector2d( enemyPos.x - thisPos.x, enemyPos.y - thisPos.y );
        double rot = angleBetween(ship.d, d)*rotAmt;

        ArrayList<Missile> M = getMissiles(gstate);
        posSum = new Vector2d(0,0);
        dirSum = new Vector2d(0,0);
        int c = 0;
        for(Missile m : M)
        {
            posSum.add(m.s);
            dirSum.add(m.v);
            c++;
        }
        dirSum.normalise();
        if(c!=0)
        {
            //System.out.println("Missiles: " + c);
            posSum.mul(1.0 / c);
            anyMissiles = true;
        }
        else
        {
            anyMissiles = false;
        }

        if(inView(ship, enemy) && shotWait <= 0)
        {
            res.shoot = true;
            shotWait = 10;
        }
        else {
            res.shoot = false;
        }

        res.thrust = thrustAmt;
        res.turn = rot;

        shotWait--;
        return res;
    }

    public void drawCircle( Graphics2D g, Vector2d center, double r )
    {
        g.drawOval((int)(center.x - r), (int)(center.y - r), (int)(r*2), (int)(r*2));
    }

    @Override
    public void draw( Graphics2D g )
    {
        AffineTransform at = g.getTransform();

        drawCircle(g, this.targetPos, this.viewRadius);
        g.setTransform(at);

        if(anyMissiles)
        {
            drawCircle(g, posSum, 4.0);
            int dx = (int)(dirSum.x * 100);
            int dy = (int)(dirSum.y * 100);
            g.drawLine((int)posSum.x - dx, (int)posSum.y - dy, (int)posSum.x + dx, (int)posSum.y + dy);
        }
    }
}
