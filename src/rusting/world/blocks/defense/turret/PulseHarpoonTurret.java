package rusting.world.blocks.defense.turret;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.ui.Bar;
import rusting.content.Palr;
import rusting.interfaces.PulseBlockc;
import rusting.world.blocks.pulse.PulseBlock.PulseBlockBuild;
import rusting.world.modules.PulseModule;

//requires Pulse to fire, lil reminder to add another effect to the bullet to compensate for the cost c:
public class PulseHarpoonTurret extends HarpoonTurret {

    //colours for charge
    public Color chargeColourStart, chargeColourEnd;

    public PulseHarpoonTurret(String name) {
        super(name);
        chargeColourStart = Palr.pulseChargeStart;
        chargeColourEnd = Palr.pulseChargeEnd;
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("power", entity -> new Bar(() ->
                Core.bundle.get("bar.pulsebalance"),
                () -> Tmp.c1.set(chargeColourStart).lerp(chargeColourEnd,
                        ((PulseBlockBuild) entity).chargef()),
                () -> Mathf.clamp(((PulseBlockBuild) entity).chargef())
        ));
    }

    public class PulseHarpoonTurretBuild extends HarpoonTurretBuild implements PulseBlockc{


        public PulseModule pulseModule = new PulseModule();

        @Override
        public PulseModule pulseModule() {
            return PulseBlockc.super.pulseModule();
        }

        /*
        @Override
        public float range() {
            return projectileRange();
        }

        @Override
        public float pulseEfficiency(){
            return Math.max(baseEfficiency, chargef(false) * timeScale());
        }

        public void customConsume(){
            pstorage.pulse -= customConsumes.pulse;
        }

        public boolean customConsumeValid(){
            return (pstorage.pulse >= customConsumes.pulse) || (!state.rules.pvp && (team == Team.derelict || team == state.rules.waveTeam && cruxInfiniteConsume));
        }

        public boolean allConsValid(){
            return customConsumeValid() && ((team == Team.derelict || (team == state.rules.waveTeam && cruxInfiniteConsume)) && !state.rules.pvp || consValid());
        }

        public boolean canRecievePulse(float pulse){
            return canRecievePulse(pulse, this);
        }

        public boolean canRecievePulse(float pulse, Building build){
            return pulse + pstorage.pulse < pulseCapacity + (canOverload ? overloadCapacity : 0);
        }

        public boolean connectableTo(){
            return connectable;
        }

        public boolean receivePulse(float pulse, Building source){
            tmpBool = canRecievePulse(pulse, source);
            if(tmpBool) addPulse(pulse, source);
            return tmpBool;
        }

        public void addPulse(float pulse){
            addPulse(pulse, null);
        }

        public void addPulse(float pulse, @Nullable Building building){
            float storage = pulseCapacity + (canOverload ? overloadCapacity : 0);
            float resistAmount = (building != this ? falloff : 0);
            pstorage.pulse += Math.max(pulse - resistAmount, 0);
            normalizePulse();
        }

        public void removePulse(float pulse){
            removePulse(pulse, null);
        }

        public void removePulse(float pulse, @Nullable Building building){
            float storage = pulseCapacity + (canOverload ? overloadCapacity : 0);
            pstorage.pulse -= pulse;
            normalizePulse();
        }

        public void normalizePulse(){
            float storage = pulseCapacity + (canOverload ? overloadCapacity : 0);
            pstorage.pulse = Math.max(Math.min(pstorage.pulse, storage), 0);
        }

        public void overloadEffect(){
            //for now, sprays projectiles around itself, and damages itself.
            if(!Vars.headless && Mathf.chance(overloadChargef() * projectileChanceModifier)) Call.createBullet(projectile, team, x, y, Mathf.random(360), projectile.damage, (float) ((Mathf.random(0.5f) + 0.3) * size), 1);
        }

        public boolean overloaded(){
            return pstorage.pulse > pulseCapacity && canOverload;
        }

        public float overloadChargef(){
            return (pstorage.pulse - pulseCapacity)/overloadCapacity;
        }

        public float chargef(boolean overloadaccount){
            return pstorage.pulse/(pulseCapacity + (canOverload && overloadaccount ? overloadCapacity : 0));
        }

        public float chargef(){
            return chargef(true);
        }

        @Override
        public float laserOffset() {
            return laserOffset;
        }

         */
    }
}
