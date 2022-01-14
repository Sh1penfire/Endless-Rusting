package rusting.interfaces;

import arc.math.geom.Vec2;

//allows me to readMount the target positions of non turret and unit blocks
public interface Targeting {
    default Vec2 targetPos(){
        return null;
    }
}
