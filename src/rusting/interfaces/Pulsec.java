package rusting.interfaces;

import arc.util.Nullable;
import mindustry.gen.Building;
import rusting.world.modules.PulseModule;

public interface Pulsec {

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

    default void removePulse(float pulse){
        removePulse(pulse, null);
    }

    default void removePulse(float pulse, @Nullable Building building){
        pulseModule().pulse -= pulse;
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
