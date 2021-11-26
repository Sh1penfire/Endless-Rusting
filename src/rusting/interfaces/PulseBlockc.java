package rusting.interfaces;

import arc.graphics.Color;
import mindustry.world.Tile;

public interface PulseBlockc extends Pulsec{

    default float pulseEfficiency(){
        return 1;
    }

    default void customConsume(){}

    default boolean customConsumeValid(){
        return false;
    }

    default boolean allConsValid(){
        return false;
    }

    default void overloadEffect(){}

    default boolean overloaded(){
        return false;
    }

    default float overloadChargef(){
        return 0;
    }

    default void drawLaser(PulseBlockc building, Color laserCol) {

    }

    default float laserOffset(){
        return 8;
    }

    default Tile tile(){
        return null;
    }

}
