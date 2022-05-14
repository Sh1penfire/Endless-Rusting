package rusting.interfaces;

import arc.graphics.Color;
import mindustry.world.Tile;

public interface PulseBlockc extends Pulsec{

    float pEfficiency();

    default void pConsume(){};

    boolean pConsValid();

    boolean allConsValid();

    default void overloadEffect(){};

    boolean overloaded();

    float laserOffset();

    Tile tile();

}
