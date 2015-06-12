package battle.controllers.Piers;

import asteroids.Action;
import asteroids.GameState;

import java.util.Random;

/**
 * Created by pwillic on 11/06/2015.
 */
public class MCTSNode {

    private static Action[] allActions;
    private static Random random = new Random();
    private static int numberOfActionsPerState = 15;

    private Action moveToThisState;

    private MCTSNode parent;
    private MCTSNode[] children;
    private int numberOfChildrenExpanded;

    private int currentDepth;

    private double totalValue;
    private int numberOfVisits = 1;

    private double explorationConstant;

    public MCTSNode(double explorationConstant) {
        this.explorationConstant = explorationConstant;
        currentDepth = 0;
    }

    private MCTSNode(MCTSNode parent, Action moveToThisState) {
        this.explorationConstant = parent.explorationConstant;
        this.moveToThisState = moveToThisState;
        this.parent = parent;
        this.children = new MCTSNode[MCTSNode.allActions.length];
        this.currentDepth = parent.currentDepth + 1;
    }

    public static void setAllActions() {
        allActions = new Action[12];
        int i = 0;
        for (double thrust = 0; thrust <= 1; thrust += 1) {
            for (double turn = -1; turn <= 1; turn += 1) {
                allActions[i] = new Action(thrust, turn, true);
                i++;
                allActions[i] = new Action(thrust, turn, false);
                i++;
            }
        }
    }

    public MCTSNode select(GameState state, int maxDepth) {
        MCTSNode current = this;
        while (current.currentDepth <= maxDepth) {
            if (current.fullyExpanded()) {
                current = current.selectBestChild();
            } else {
                return current.expand();
            }
        }
        return current;
    }

    public MCTSNode expand() {
        int childToExpand = random.nextInt(allActions.length);
        while (children[childToExpand] != null) {
            childToExpand = random.nextInt(allActions.length);
        }
        children[childToExpand] = new MCTSNode(this, allActions[childToExpand]);
        return children[childToExpand];
    }

    public MCTSNode selectBestChild() {
        double bestScore = children[0].calculateChild();
        int bestIndex = 0;

        for (int i = 1; i < numberOfChildrenExpanded; i++) {
            double childScore = children[i].calculateChild();
            if (childScore > bestScore) {
                bestScore = childScore;
                bestIndex = i;
            }
        }
        return children[bestIndex];
    }

    public double calculateChild() {
        return (totalValue / numberOfVisits) +
                (explorationConstant * (Math.sqrt(Math.log(parent.numberOfVisits) / numberOfVisits)));
    }

    public void updateValues(double value) {
        MCTSNode current = this;
        while (current.parent != null) {
            current.numberOfVisits++;
            // todo work out scoring system.
            current = current.parent;
        }
    }

    public double rollout(GameState state) {
        return 0.0d;
    }

    public Action getBestAction() {
        double bestScore = children[0].totalValue;
        int bestIndex = 0;

        for (int i = 1; i < numberOfChildrenExpanded; i++) {
            double childScore = children[i].totalValue;
            if (childScore > bestScore) {
                bestScore = childScore;
                bestIndex = i;
            }
        }
        return children[bestIndex].moveToThisState;
    }

    private boolean fullyExpanded() {
        return numberOfChildrenExpanded == allActions.length;
    }

}
