package battle.controllers.webpigeon;

import asteroids.Action;

/**
 * Created by jwalto on 12/06/2015.
 */
public class GaController {
    private double[] genome;

    public GaController(double[] genome) {
        this.genome = genome;
    }

    public Action getMove() {
        return new Action(0,0, false);
    }
}
