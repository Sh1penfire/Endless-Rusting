package rusting.interfaces;

import arc.math.geom.Position;
import arc.util.Nullable;
import rusting.world.modules.PulseModule;

public interface Pulsec extends Position {

    boolean canReceivePulse(float pulse, Pulsec source);

    boolean connectableTo();

    void addPulse();

    float addPulse(float pulse);

    float addPulse(float pulse, @Nullable Pulsec source);

    float removePulse(float pulse);

    float removePulse(float pulse, @Nullable Pulsec source);

    void normalizePulse();

    void normalizeOverload();

    float chargef();

    float chargef(boolean overloadaccount);

    float overloadf();

    PulseModule pulseModule();

    PulseModule overloadModule();
}
