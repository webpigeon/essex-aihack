package battle.controllers;

import asteroids.Action;
import battle.BattleController;
import battle.NeuroShip;
import battle.RenderableBattleController;
import battle.SimpleBattle;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by davidgundry on 11/06/15.
 */
public class FireForwardController implements RenderableBattleController {
    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {
        return new Action(1,0,true);
    }

    @Override
    public void render(Graphics2D g, NeuroShip s) {
        AffineTransform at = g.getTransform();
        g.translate(s.s.x, s.s.y);
        double rot = Math.atan2(s.d.y, s.d.x) + Math.PI / 2;
        g.rotate(rot);
        /*g.scale(scale, scale);
        g.fillPolygon(xp, yp, xp.length);
        if (thrusting) {
            g.setColor(Color.red);
            g.fillPolygon(xpThrust, ypThrust, xpThrust.length);
        }
        g.setTransform(at);

        g.setColor(Color.GRAY);
        g.drawLine(0, 0, 1, 0);*/
    }
}
