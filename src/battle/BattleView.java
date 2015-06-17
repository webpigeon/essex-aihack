package battle;

import asteroids.GameObject;
import asteroids.GameState;
import asteroids.Ship;
import math.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import static asteroids.Constants.*;
import static java.awt.Color.black;


public class BattleView extends JComponent {
    static int offset = 0;
    int scale;
    // static int carSize = 5;
    static Color bg = black;
    SimpleBattle game;
    // Font font;

    Ship ship;

    static double viewScale = 1.0;


    public BattleView(SimpleBattle game) {
        this.game = game;
        scale = size.width - 2 * offset;

    }

    public void paintComponent(Graphics gx) {
        Graphics2D g = (Graphics2D) gx;
        AffineTransform at = g.getTransform();
        g.translate((1 - viewScale) * width / 2, (1-viewScale)*height / 2);

        // this was an experiment to turn it into a side-scroller
        // but it produces a weird moving screen effect
        // needs more logic in the drawing process
        // to wrap the asteroids that have been projected off the screen
        // g.translate(-(game.ship.s.x - width/2), 0);

        g.scale(viewScale, viewScale);

        game.draw(g);
        g.setTransform(at);
        paintState(g);
    }


    public void paintState(Graphics2D g) {

        for (GameObject object : game.objects) {
            object.draw(g);
        }

        g.setColor(Color.white);
        g.setFont(font);
        // String str = "" + game.score + " : " + game.list.nShips() + " : " + game.state
        //         + " : " + game.list.isSafe(game.ship) + " : " + game.nLives;
        // FontMetrics fm = font.

        if (game.hasNotStarted()) {
            g.drawString("Battle Asteroids (version "+game.code+")", 10, 50);
            g.drawString("press q to start", 10, 100);
            return;
        } else if (game.isGameOver()) {
            g.drawString("Game over", 10, 100);
            g.drawString("Please complete the next questionnaire part now (game "+game.code+")", 10, 50);
            g.drawString("Press q to start", 10, 150);
            return;
        }

        //String str = game.stats.get(0) + " " + game.stats.get(1) + " " + game.currentTick;
        SimpleBattle.PlayerStats p1Stats = game.stats.get(0);
        SimpleBattle.PlayerStats p2Stats = game.stats.get(1);
        String strScores    = "Score player: " + p1Stats.getPoints() + " | enemy: " + p2Stats.getPoints();
        String strMissiles  = "Missiles left player: " + game.getMissilesLeft(0) + " | enemy: " + game.getMissilesLeft(1);
        String strTicks     = "Remaining time:    " + (1000-game.getTicks());
        /*String p1 = "P1 Green " + game.p1.getClass().getSimpleName();
        String p2 = "P2 Blue " + game.p2.getClass().getSimpleName();*/
        g.drawString(strScores, 10, 20);
        g.drawString(strMissiles, 10, 50);
        g.drawString(strTicks, 10, 80);
        //g.drawString(p1, 10, 110);
        //g.drawString(p2, 10, 140);
    }


    public Dimension getPreferredSize() {
        return size;
    }


}
