package rusting.game.waves;

import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.util.pooling.Pool.Poolable;
import mindustry.type.UnitType;

/**
 * A class used to represent sources f units in the world, such as spawn points or special buildings which spawn units.
 */
public abstract class UnitSource implements Poolable {
    public abstract UnitSourceInfo amount(float time);

    public abstract Vec2 worldPos();

    public class UnitSourceInfo{
        public ObjectMap<UnitType, Float> incoming;
    }
}
