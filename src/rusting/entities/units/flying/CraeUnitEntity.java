package rusting.entities.units.flying;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;
import rusting.content.*;
import rusting.entities.units.BaseUnitEntity;
import rusting.entities.units.CraeUnitType;
import rusting.interfaces.Pulsec;

import static mindustry.Vars.state;

public class CraeUnitEntity extends BaseUnitEntity implements Pulsec {

    //blacklisted effects for the sake of the ai
    private static Seq<String> blacklistedStatusEffects = Seq.with("betamindy-amnesia");
    private float shake = 0;
    public float xOffset = 0, yOffset = 0;
    public float alphaDraw = 0;
    public float pulse = 0;
    private Team lastTeam;

    public CraeUnitType unitType(){
        return type instanceof CraeUnitType ? (CraeUnitType) type : null;
    }

    public void addPulse(float pulse){
        this.pulse += pulse;
        clampPulse();
    }

    public void clampPulse(){
        pulse = Math.max(Math.min(pulse, unitType().pulseStorage), 0);
    }

    public float chargef(){
        return pulse/unitType().pulseStorage;
    }

    @Override
    public boolean canShoot() {
        return !disarmed && (!(isFlying() && type.canBoost) || type.flying && isFlying());
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
        //self explanatory, since the units shoudn't be able to change teams
        if(lastTeam == null) lastTeam = team;
        if(team != lastTeam) team = lastTeam;
        float timeOffset = 3;
        if(shake >= timeOffset){
            xOffset = (float) (hitSize/8 * 0.3 * Mathf.range(2));
            yOffset = (float) (hitSize/8 * 0.3 * Mathf.range(2));
        }
        else shake++;
        alphaDraw = Mathf.absin(Time.time/100, chargef(), 1);
    }

    @Override
    public void draw() {
        super.draw();
        Draw.reset();
        if(pulse > 0) {
            if(elevation < 0.9) Draw.z(Layer.bullet);
            else if(type().lowAltitude) Draw.z(Layer.flyingUnitLow + 0.1f);
            else Draw.z(Layer.flyingUnit + 0.1f);

            float rotation = this.rotation - 90;

            Draw.color(unitType().chargeColourStart, unitType().chargeColourEnd, chargef());
            Draw.alpha(alphaDraw * unitType().overloadedOpacity);
            TextureRegion chargeRegion = unitType().pulseRegion;
            TextureRegion shakeRegion = unitType().shakeRegion;
            Draw.rect(shakeRegion, x + xOffset, y + yOffset, (chargeRegion.width + yOffset)/4, (chargeRegion.height + xOffset)/4, rotation);
            Draw.rect(chargeRegion, x, y, rotation);
            Draw.alpha((float) (alphaDraw * unitType().overloadedOpacity * 0.5));
            Draw.rect(chargeRegion, x, y, (float) (chargeRegion.height * 1.5/4), (float) (chargeRegion.width * 1.5/4), rotation);
        }
    }

    @Override
    public void destroy() {
        if(!isAdded()) return;
        float power = chargef() * 150.0F;
        float explosiveness = 1F + pulse/3.5f;
        if (!spawnedByCore) {
            Damage.dynamicExplosion(x, y, 0, explosiveness, power, bounds() / 2.0F, state.rules.damageExplosions, item().flammability > 1, team);
            int bulletSpawnInterval = type instanceof CraeUnitType ? unitType().projectileDeathSpawnInterval : 10;
            for(int i = 0; i < chargef() * bulletSpawnInterval; i++){
                RustingBullets.craeBolt.create(this, x, y, Mathf.random(360));
                RustingBullets.craeShard.create(this, team, x, y, Mathf.random(360), 0.25f * Mathf.random(1.1f), hitSize/RustingBullets.craeShard.range() * 32 * Mathf.random(1.1f));
                RustingBullets.craeShard.create(this, team, x, y, Mathf.random(360), 0.25f * Mathf.random(1.4f), hitSize/RustingBullets.craeShard.range() * 32 * Mathf.random(1.4f));
            }
        }
        if(pulse != 0) Fxr.pulseSmoke.at(x, y, rotation, new float[]{Math.min(chargef() * 3, 1) * hitSize * 5 + 16 + hitSize * 2, chargef() * hitSize / 2 + 3 * chargef(), 1});
        super.destroy();
    }

    @Override
    public void write(Writes w) {
        super.write(w);
        w.f(pulse);
    }

    @Override
    public void read(Reads r){
        super.read(r);
        pulse = r.f();
        clampPulse();
    }

    @Override
    public int classId(){
        return RustingUnits.classID(CraeUnitEntity.class);
    }

}
