package rusting.interfaces.examples;

import arc.math.Mathf;
import rusting.interfaces.Pulsec;
import rusting.world.modules.PulseModule;

//A dummy class used as an example of how to use Pulsec
public class PulseEntity implements Pulsec {
    public float pulseCapacity = 100;
    public float overloadCapacity = 400;

    public float drain = 1;

    public boolean closed = true;

    public float x = 0, y = 0;
    public PulseModule storage = new PulseModule();
    public PulseModule overload = new PulseModule();

    public void update(){
        overload.pulse -= drain * overloadf() * overloadf();
        normalizePulse();
    }

    @Override
    public boolean canReceivePulse(float pulse, Pulsec source) {
        return !closed;
    }

    @Override
    public boolean connectableTo() {
        return true;
    }

    @Override
    public void addPulse() {
        storage.pulse = pulseCapacity;
    }

    @Override
    public float addPulse(float pulse) {
        return addPulse(pulse, this);
    }

    @Override
    public float addPulse(float pulse, Pulsec source) {
        float before = totalPulse();
        overload.pulse += (storage.pulse += pulse) % pulseCapacity;
        normalizePulse();
        return totalPulse() - before;
    }

    public float totalPulse(){
        return storage.pulse + overload.pulse;
    }

    @Override
    public float removePulse(float pulse) {
        return removePulse(pulse, this);
    }

    @Override
    public float removePulse(float pulse, Pulsec source) {
        float before = totalPulse();
        overload.pulse += (storage.pulse += pulse) % pulseCapacity;
        normalizePulse();
        return totalPulse() - before;
    }

    @Override
    public void normalizePulse() {
        storage.pulse = Mathf.clamp(storage.pulse, 0, pulseCapacity);
        overload.pulse = Mathf.clamp(overload.pulse, 0, pulseCapacity);
    }

    @Override
    public void normalizeOverload() {
        overload.pulse = Mathf.clamp(overload.pulse, 0, pulseCapacity);
    }

    @Override
    public float chargef() {
        return chargef(false);
    }

    @Override
    public float chargef(boolean overloadaccount) {
        return overloadaccount ? storage.pulse/ pulseCapacity : (storage.pulse + overload.pulse)/(pulseCapacity + overloadCapacity);
    }

    @Override
    public float overloadf(){
        return overload.pulse/ overloadCapacity;
    }

    @Override
    public PulseModule pulseModule() {
        return storage;
    }

    @Override
    public PulseModule overloadModule() {
        return overload;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
