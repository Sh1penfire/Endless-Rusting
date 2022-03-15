package rusting.entities.units.flying;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.StatusEffects;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import rusting.content.RustingUnits;
import rusting.entities.units.BaseUnitEntity;
import rusting.entities.units.CraeUnitType;
import rusting.interfaces.Pulsec;
import rusting.world.modules.PulseModule;

public class CraeUnitEntity extends BaseUnitEntity implements Pulsec {

    public PulseModule storage = new PulseModule();
    public PulseModule overload = new PulseModule();

    //blacklisted effects for the sake of the ai
    private static Seq<String> blacklistedStatusEffects = Seq.with("betamindy-amnesia");
    private float shake = 0;
    public float xOffset = 0, yOffset = 0;
    public float alphaDraw = 0;
    private Team lastTeam;
    public CraeUnitType cType = null;

    @Override
    public void setType(UnitType type) {
        super.setType(type);
        cType = unitType();
    }

    public CraeUnitType unitType(){
        return type instanceof CraeUnitType ? (CraeUnitType) type : null;
    }

    @Override
    public void apply(StatusEffect status, float time){
        if(status != StatusEffects.none && status != null){
            if(this.isImmune(status) || blacklistedStatusEffects.contains(status.name)) status.effect.at(x, y);
            else if(status.damage * 60 * 4 < unitType().health && status.speedMultiplier > 0.15f && status.reloadMultiplier > 0.15 && status.damageMultiplier > 0.15 && status.healthMultiplier > 0.55) super.apply(status, time);
            else if(status.permanent == true && status.damage > 0) this.heal(Math.abs(status.damage) * 60);
        }
    }

    public boolean technicallyImmune(StatusEffect effect) {
        return type.immunities.contains(effect) || blacklistedStatusEffects.contains(effect.name) || !(effect.damage * 60 * 4 < unitType().health && effect.speedMultiplier > 0.15f && effect.reloadMultiplier > 0.15 && effect.damageMultiplier > 0.15 && effect.healthMultiplier > 0.55);
    }

    @Override
    public void collision(Hitboxc other, float x, float y) {
        super.collision(other, x, y);
        if(other instanceof Bullet){
            Bullet otherBullet = (Bullet) other;
            if(technicallyImmune(otherBullet.type.status) && otherBullet.type.reflectable) {
                if (otherBullet.owner instanceof Healthc) ((Healthc) otherBullet.owner).damagePierce(otherBullet.type.damage/5);
                otherBullet.type.create(otherBullet.owner, team, x, y, angleTo(otherBullet), otherBullet.vel.len()/otherBullet.type.speed,  otherBullet.fout()).collided = (otherBullet.collided);
                otherBullet.remove();
            }
        }
    }

    @Override
    public void update() {
        super.update();
        //self explanatory, since the units shoudn't be able to change teams, period.
        if(lastTeam == null || controller instanceof Player) lastTeam = team;
        if(team != lastTeam) team = lastTeam;
    }

    @Override
    public void draw() {
        super.draw();
        Draw.reset();
    }

    @Override
    public void destroy() {
        if(!isAdded()) return;
        super.destroy();
    }


    @Override
    public boolean canReceivePulse(float pulse, Pulsec source) {
        return true;
    }

    @Override
    public boolean connectableTo() {
        return true;
    }

    @Override
    public void addPulse() {
        storage.pulse = cType.pulseCapacity;
    }

    @Override
    public float addPulse(float pulse) {
        return addPulse(pulse, this);
    }

    @Override
    public float addPulse(float pulse, Pulsec source) {
        float before = totalPulse();
        overload.pulse += Mathf.clamp((storage.pulse += pulse) - cType.pulseCapacity, 0, cType.overloadCapacity);
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
        storage.pulse -= pulse;
        normalizePulse();
        return before - totalPulse();
    }

    @Override
    public void normalizePulse() {
        storage.pulse = Mathf.clamp(storage.pulse, 0, cType.pulseCapacity);
        overload.pulse = Mathf.clamp(overload.pulse, 0, cType.overloadCapacity);
    }

    @Override
    public void normalizeOverload() {
        overload.pulse = Mathf.clamp(overload.pulse, 0, cType.pulseCapacity);
    }

    @Override
    public float chargef() {
        return chargef(false);
    }

    @Override
    public float chargef(boolean overloadaccount) {
        return overloadaccount ? (storage.pulse + overload.pulse)/(cType.pulseCapacity + cType.overloadCapacity) : storage.pulse/cType.pulseCapacity;
    }

    @Override
    public float overloadf(){
        return overload.pulse/cType.overloadCapacity;
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

    @Override
    public void write(Writes w) {
        super.write(w);
        w.f(storage.pulse);
        w.f(overload.pulse);
    }

    @Override
    public void read(Reads r, byte revision) {
        super.read(r, revision);
        storage.pulse = r.f();
        overload.pulse = r.f();
    }

    @Override
    public int classId(){
        return RustingUnits.classID(CraeUnitEntity.class);
    }
}
