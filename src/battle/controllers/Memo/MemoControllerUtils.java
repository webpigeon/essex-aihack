package battle.controllers.Memo;

import math.Vector2d;

/**
 * Created by Memo Akten on 11/06/2015.
 */
public class MemoControllerUtils {

    static double lookAt(Vector2d s, Vector2d d, Vector2d lookat, double threshold) {
        Vector2d desired_rot_vec = lookat.copy();
        desired_rot_vec.add(s, -1);

        double current_rot = Math.atan2(d.y, d.x);
        double target_rot = Math.atan2(desired_rot_vec.y, desired_rot_vec.x);
        if(Math.abs(current_rot - target_rot) < threshold) {
            return 0;
        } else {
            if(current_rot > target_rot) return -1;
            else return 1;
        }
    }


}
