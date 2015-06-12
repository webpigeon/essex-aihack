package battle.controllers;

import asteroids.Action;
import battle.BattleController;
import battle.NeuroShip;
import battle.SimpleBattle;

import java.util.Random;

/**
 * Created by pwillic on 11/06/2015.
 *
 * Features to use for avoidance
 *
 *  1. Distance to enemy - 0 very close, 1 very far
 *  2. Enemy in FL - 0 no, 1 yes
 *  3. Enemy in FR
 *  4. Enemy in BL
 *  5. Enemy in BR
 *
 *  Features to use for shooting
 *  1. Distance to enemy
 *  2. Deviance from dead straight Left - 0.5 no deviance, 0 full left, 1 full right
 *  3. Missiles Remaining
 *
 *  Features to use for aligning
 *  1. Distance to enemy
 *  2. Deviance from enemy
 *  3.
 */
public class PiersController implements BattleController {
    Neuron tinyBrain;


    public PiersController() {
        tinyBrain = new Neuron(10);
    }


    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId) {

        double[] avoidanceTable = new double[5];
        double[] shootingTable = new double[3];


        return null;
    }
}

class Neuron {
    private static Random random = new Random();
    int numberOfInputs;
    double[] neuronWeights;

    // Random neuron
    public Neuron(int numberOfInputs) {
        this.numberOfInputs = numberOfInputs;
        this.neuronWeights = new double[numberOfInputs];
        for (int i = 0; i < neuronWeights.length; i++) {
            neuronWeights[i] = random.nextDouble();
        }
    }

    // clone a neuron / make one
    public Neuron(double[] neuronWeights) {
        this.numberOfInputs = neuronWeights.length;
        this.neuronWeights = neuronWeights;
    }

    public double think(double[] inputs) {
        if (inputs.length != numberOfInputs)
            throw new IllegalArgumentException("Wrong number of inputs" + inputs.length + " - should be: " + numberOfInputs);
        double totalSum = 0;
        for (int index = 0; index < numberOfInputs; index++) {
            totalSum += (inputs[index] * neuronWeights[index]);
        }
        return totalSum;
    }
}