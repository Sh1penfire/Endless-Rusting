package rusting.interfaces.block;

import mindustry.gen.Building;
import rusting.interfaces.PulseInstantTransportation;

public interface PulseCanalc extends PulseInstantTransportation {

    default boolean canConnect(Building b){
        return false;
    }

    default boolean canReceive(Building b){return false;}
}
