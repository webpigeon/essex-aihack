package battle.controllers;

import asteroids.Action;
import asteroids.Controller;
import asteroids.GameState;
import asteroids.GameObject;
import asteroids.Ship;
import battle.RenderableBattleController;
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
public class DaniController implements RenderableBattleController
{

    Action action;
    Vector2d targetPos;
    double viewRadius = 20.0;
    double thrustAmt = 1.5;
    double rotAmt = 1.5;
    int shotWait = 0;

    Vector2d meanMissilePos;
    Vector2d meanMissileDir;
    Vector2d missileLineA;
    Vector2d missileLineB;
    Vector2d segp;

    boolean anyMissiles = false;

    Vector2d targetPosition;
    Vector2d targetDirection;

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
        Vector2d d = new Vector2d(ship.d, true);
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
        Vector2d v = Vector2d.subtract(b, a);
        Vector2d w = Vector2d.subtract(p, a);

        double d1 = dot(w,v);
        if( d1 <= 0.0 )
            return a;
        double d2 = dot(v,v);
        if( d2 <= d1 )
            return b;

        double t = d1/d2;
        return Vector2d.add(a, Vector2d.multiply(v, t));
    }

    double           distanceToSegment( Vector2d p, Vector2d a, Vector2d b )
    {
        return p.dist( closestPointOnSegment(p,a,b) );
    }

    Vector2d getPerp( Vector2d v )
    {
        return new Vector2d(-v.y, v.x);
    }

    Vector2d keepDistanceVector( SimpleBattle gstate, Vector2d shipPos, Vector2d enemyPos )
    {
        if( shipPos.dist(enemyPos) < 200 )
            return Vector2d.multiply( Vector2d.subtract(shipPos, enemyPos), 0.3 );

        return new Vector2d(0,0);
    }


    Vector2d avoidBulletsVector( SimpleBattle gstate, Vector2d shipPos )
    {
        ArrayList<Missile> M = getMissiles(gstate);
        meanMissilePos = new Vector2d(0,0, true);
        meanMissileDir  = new Vector2d(0,0, true);

        int c = 0;
        for(Missile m : M)
        {
            meanMissilePos.add(m.s);
            meanMissileDir.add(m.v);
            c++;
        }

        // no missiles bail out
        if(c==0)
        {
            anyMissiles = false;
            System.out.println("No missiles");
            return new Vector2d(0,0,true);
        }

        meanMissilePos.divide(c);
        meanMissileDir.normalise();
        // project onto main missile line
        double min = 1000000;
        double max = -1000000;

        for(Missile m : M)
        {
            Vector2d p = new Vector2d(m.s,true);
            p.subtract(meanMissilePos);
            double d = dot(p, meanMissileDir);
            if( d < min )
                min = d;
            if( d > max )
                max = d;
        }

        missileLineA = Vector2d.add( meanMissilePos, Vector2d.multiply(meanMissileDir, min) );
        missileLineB = Vector2d.add( meanMissilePos, Vector2d.multiply(meanMissileDir, max) );

        anyMissiles = true;

        segp = closestPointOnSegment( shipPos, missileLineA, missileLineB );
        if( segp.dist(shipPos) < 100 )
        {
            Vector2d vavoid = Vector2d.subtract( shipPos, segp );
            return Vector2d.multiply(vavoid, 1) ;
        }

        return new Vector2d(0,0,true);
    }

    @Override
    public Action getAction(SimpleBattle gstate, int playerId)
    {
        Action res = new Action(0,0,false);
        NeuroShip ship = gstate.getShip(playerId);
        NeuroShip enemy = gstate.getShip((playerId == 1)?0:1);

        Vector2d enemyPos = enemy.s;
        Vector2d thisPos = ship.s;

        // Direction towards enemy
        Vector2d d = new Vector2d( enemyPos.x - thisPos.x, enemyPos.y - thisPos.y, true );
        Vector2d vavoid = avoidBulletsVector( gstate, thisPos );
        d.add(vavoid);
        d.add( keepDistanceVector(gstate, thisPos, enemyPos) );

        double rot = angleBetween(ship.d, d)*rotAmt;

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

    public void drawLine( Graphics2D g, Vector2d a, Vector2d b )
    {
        g.drawLine((int)a.x, (int)a.y, (int)b.x, (int)b.y);
    }

    @Override
    public void render( Graphics2D g, NeuroShip s )
    {
        AffineTransform at = g.getTransform();

        drawCircle(g, this.targetPos, this.viewRadius);
        g.setTransform(at);

        if(anyMissiles)
        {
            drawCircle(g, meanMissilePos, 4.0);
//            int dx = (int)(meanMissileDir.x * 100);
//            int dy = (int)(meanMissileDir.y * 100);
            drawLine( g, Vector2d.subtract(meanMissilePos, Vector2d.multiply(meanMissileDir, 100)),
                    Vector2d.add(meanMissilePos, Vector2d.multiply(meanMissileDir, 100)) );
//            g.drawLine((int)meanMissilePos.x - dx, (int)meanMissilePos.y - dy, (int)meanMissilePos.x + dx, (int)meanMissilePos.y + dy);
            drawCircle(g, missileLineA, 7.0);
            drawCircle(g, missileLineB, 7.0);
        }
    }
}
