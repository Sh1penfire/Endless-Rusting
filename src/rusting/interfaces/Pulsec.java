package rusting.interfaces;

import arc.math.geom.Position;
import arc.util.Nullable;
import mindustry.gen.Building;
import rusting.world.modules.PulseModule;

public interface Pulsec extends Position {

    default boolean canReceivePulse(float pulse, Pulsec source){
        return false;
    }

    default boolean connectableTo(){
        return false;
    }

    default boolean receivePulse(float pulse, Pulsec source){;
        pulseModule().pulse += pulse;
        return true;
    }

    default void addPulse(){
        return;
    }

    default void addPulse(float pulse){
        addPulse(pulse, null);
    }

    default void addPulse(float pulse, @Nullable Pulsec building){
        pulseModule().pulse += pulse;
    }

    //returns how much pulse was removed
    default float removePulse(float pulse){
        return removePulse(pulse, null);
    }

    default float removePulse(float pulse, @Nullable Building building){
        float before = pulseModule().pulse;
        pulseModule().pulse -= pulse;
        normalizePulse();
        return before - pulseModule().pulse;
    }

    default void normalizePulse(){}


    default float chargef(boolean overloadaccount){
        return 0;
    }

    default float chargef(){
        return chargef(true);
    }

    default PulseModule pulseModule(){
        return null;
    }
}
