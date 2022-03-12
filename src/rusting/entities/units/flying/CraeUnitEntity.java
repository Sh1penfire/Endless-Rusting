package rusting.entities.units.flying;

import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.StatusEffects;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.StatusEffect;
import rusting.content.RustingUnits;
import rusting.entities.units.BaseUnitEntity;
import rusting.entities.units.CraeUnitType;

public class CraeUnitEntity extends BaseUnitEntity {

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
    public void write(Writes w) {
        super.write(w);
        w.f(pulse);
    }

    @Override
    public void read(Reads r){
        super.read(r);
        pulse = r.f();
    }

    @Override
    public int classId(){
        return RustingUnits.classID(CraeUnitEntity.class);
    }

}
