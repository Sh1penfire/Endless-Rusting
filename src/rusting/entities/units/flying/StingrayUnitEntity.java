package rusting.entities.units.flying;

import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import rusting.content.*;

public class StingrayUnitEntity extends AntiquimGuardianUnitEntity {
    public Seq<Trail> trailSeq = Seq.with(new Trail(6), new Trail(6));
    public static Seq<Vec2> trailPos = Seq.with(new Vec2(18.75f, -4.7f), new Vec2(-18.75f, -4.7f));
    private static float tmpFloat, tmpFloat2;
    public float shieldCharge = 0;
    public float addableShieldCharge = 0;
    public float currentDamageTT = 0;

    public void damage(float amount) {
        if(iframes <= 0) {
            super.damage(Math.min(amount, 2000 - shieldCharge));
            shieldCharge += Math.max(amount/healthMultiplier, 0);
        }
        else if(iframes >= 200) iframes = Math.max(iframes - amount/5/healthMultiplier, 180);
        else iframes -= Math.min(amount/5/healthMultiplier, 1.75f/healthMultiplier);
    }

    @Override
    public void damagePierce(float amount) {
        super.damagePierce(amount);
        iframes -= amount;
    }

    @Override
    public void collision(Hitboxc other, float x, float y) {
        super.collision(other, x, y);
        if(other instanceof Bullet) {
            Bullet b = (Bullet) other;
            if(b.type.reflectable && b.damage > 235 && iframes > 0){
                if(b.type.pierce == true && b.type.pierceCap > 0 && b.collided.size < b.type.pierceCap){
                    b.team = team;
                    b.rotation(angleTo(b));
                }
                else b.type.create(this, team, x, y, angleTo(b) + 180);
            }
            else {
                if (b.damage >= 5) b.damage -= 5;
                else b.time = b.lifetime;
            }
        }
    }

    @Override
    public void update() {
        super.update();
        Tmp.v1.set(trailPos.get(0)).rotate(rotation - 90);
        trailSeq.get(0).update(x + Tmp.v1.x, y + Tmp.v1.y);
        Tmp.v1.set(trailPos.get(1)).rotate(rotation - 90);
        trailSeq.get(1).update(x + Tmp.v1.x, y + Tmp.v1.y);
        if(shieldCharge >= 2000) {
            shieldCharge += addableShieldCharge;
            addableShieldCharge = 1500;
            shieldCharge = 0;
            tmpFloat2 = 120;
            Fxr.stingrayShieldPop.at(x, y, rotation, this);
            Units.nearby(x - tmpFloat2/2, y - tmpFloat2/2, tmpFloat2, tmpFloat2, u -> {
                if(dst(u) <= tmpFloat2){
                    u.impulse(Tmp.v1.trns(angleTo(u), (tmpFloat2 - dst(u))/tmpFloat2 * 250));
                }
            });
        }
        if(health < maxHealth) iframes = Math.max(iframes - Time.delta/2, 0);
        if(addableShieldCharge > 0){
            iframes = Math.min(iframes + Math.min(addableShieldCharge, 5 * Time.delta), 2000);
            addableShieldCharge = Math.max(addableShieldCharge - 5 * Time.delta, 0);
        }
    }

    @Override
    public void draw() {
        Draw.reset();
        Draw.z(Layer.flyingUnit - 0.1f);
        trailSeq.each(t -> t.draw(Palr.lightstriken, 4));
        if(iframes > 0){
            Draw.z(Layer.flyingUnit + 0.1f);
            tmpFloat = Math.min(Math.max(iframes/200, 0), 1);
            tmpFloat2 = hitSize * 2 - 3 + 3 * tmpFloat;
            Tmp.c1.set(team.color).lerp(Palr.pulseBullet, tmpFloat).a(tmpFloat/7);
            Tmp.c2.set(Palr.pulseChargeEnd).a(tmpFloat);
            Fill.light(x, y, 14, tmpFloat2 + 0.25f, Tmp.c1, Tmp.c2);
            Draw.color(Tmp.c2.a(1), Palr.pulseBullet, tmpFloat);
            Lines.stroke(3 * tmpFloat);
            Lines.circle(x, y, tmpFloat2);
            if(tmpFloat < 1) for (int i = 0; i < 5; i++) {
                Tmp.v1.trns(tmpFloat * 360 + i * 72, tmpFloat2 * tmpFloat);
                Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 2 * tmpFloat * Mathf.sin(Time.time/3));
                Tmp.v1.trns(tmpFloat * -360 + i * 72, tmpFloat2 * tmpFloat);
                Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 2 * tmpFloat * Mathf.sin(Time.time/3));
            }
        }
        else if (shieldCharge > 0 && hitTime > 0){
            Draw.z(Layer.flyingUnit + 0.1f);
            tmpFloat = Math.min(Math.max(shieldCharge/2000, 0), 1);
            tmpFloat2 = hitSize * 2 - 3 + 3 * tmpFloat;
            Draw.color(Palr.pulseChargeEnd, Palr.pulseBullet, tmpFloat);
            Lines.stroke(3 * tmpFloat * hitTime);
            Lines.circle(x, y, tmpFloat2);
        }
        super.draw();
    }

    @Override
    public boolean damaged() {
        return super.damaged() && iframes <= 0 || health == maxHealth && iframes < shieldCharge;
    }

    @Override
    public void heal(float amount) {
        if(health < maxHealth) {
            if(iframes > 0) amount /= 5;
            super.heal(amount);
        }
        else addableShieldCharge = Math.min(addableShieldCharge + amount, 2000);
    }

    @Override
    public void write(Writes w) {
        super.write(w);
        w.f(shieldCharge);
        w.f(addableShieldCharge);
    }

    @Override
    public void read(Reads r) {
        super.read(r);
        shieldCharge = r.f();
        addableShieldCharge = r.f();
    }

    @Override
    public String toString() {
        return "StingrayUnitEntity#" + id;
    }

    @Override
    public int classId(){
        return RustingUnits.classID(StingrayUnitEntity.class);
    }
}
