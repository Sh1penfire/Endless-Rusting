package rusting.math;

import arc.func.Cons;

public class Angler {
    //returns an angle in a range of @points offset by @offset
    public static float shotgunStep(int step, int points, float spacing, float offset){
        return step * spacing - (points - 1) * spacing/2 + offset;
    }

    public static void shotgun(int points, float spacing, float offset, Cons<Float> prov){
        for(int i = 0; i < points; i++) {
            prov.get(shotgunStep(i, points, spacing, offset));
        }
    }
}
