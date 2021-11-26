package rusting.interfaces;

import mindustry.gen.Building;

public interface PulseCanalc extends PulseInstantTransportation{

    default boolean canConnect(Building b){
        return false;
    }

    default boolean canReceive(Building b){return false;}
}
