package battle.controllers.Memo;

import math.Vector2d;
import asteroids.Action;

/**
 * Created by Memo Akten on 11/06/2015.
 */
public class MemoControllerUtils {

    // returns turn value (-1, 0, 1)
    static double lookAt(Vector2d s, Vector2d d, Vector2d lookat, double rot_threshold) {
        Vector2d desired_rot_vec = new Vector2d(lookat, true);

        desired_rot_vec.add(s, -1);

        double current_rot = Math.atan2(d.y, d.x);
        double target_rot = Math.atan2(desired_rot_vec.y, desired_rot_vec.x);
        if(Math.abs(current_rot - target_rot) < rot_threshold) {
            return 0;
        } else {
            if(current_rot > target_rot) return -1;
            else return 1;
        }
    }

    // fills in Action with turn and thrust
    static double thrustTo(Vector2d s, Vector2d d, Vector2d desired_pos, double dist_threshold, double rot_threshold, Action action) {
        double dist_to_desired_pos = s.dist(desired_pos);
        if(dist_to_desired_pos < dist_threshold) {
            action.thrust = 0;
        } else {
            action.thrust = 1;
            action.turn = lookAt(s, d, desired_pos, rot_threshold);
        }
        return action.thrust;
    }

}
