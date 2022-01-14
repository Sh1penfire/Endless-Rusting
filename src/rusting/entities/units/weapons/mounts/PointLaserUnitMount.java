package rusting.entities.units.weapons.mounts;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;
import rusting.content.Fxr;
import rusting.content.Palr;
import rusting.graphics.JagedTrail;
import rusting.graphics.PoolableTrail;

import static arc.util.Time.time;

public class PointLaserUnitMount extends ShootUnitMount{

    public PointLaserUnitMount(){
        super();
        name = "pointlaser";
    }

    public static Vec2 tr = new Vec2();

    public float range = 280;

    public float duration = 360;

    public float shootDst = 4.5f;

    public float width = 3, shineBaseWidth = 6, length = 45;
    public float stumpOffset = 8;
    public float laserSpeed = 3.5f;

    public int shineSides = 8;
    public float shineCycle = 0.025f, shineCycleMag = 0.75f, shineWidth = 3, shineLength = 23, shineSize = 5, shineVary = 3;
    public float beamSecWidth = 2.5f;
    public float statusDuration = 660;
    public StatusEffect status = StatusEffects.none;
    public float damage = 1.5f;

    public float hitEffectChance = 0.05f;
    public Effect hitEffect = Fxr.craeWeaversResidue;

    public float passiveEffectChance = 0.25f;
    public Effect passiveEffect = Fxr.craeBeamHit;
    public Color frontColor = Palr.pulseShieldStart, backColor = Palr.pulseChargeEnd, heatColor = Palr.pulseChargeStart;

    //laser position is it's own thing
    public Vec2 laserPosition = new Vec2(x, y), targetPosition = new Vec2(x, y);

    //shoot duration is how long the turret has left to shoot, charge is the progress to being fully charged and the reload is the normal reload value
    public float shootDuration = 0, reload = 0;

    public PoolableTrail trail = new PoolableTrail(25);
    public JagedTrail jtrail = new JagedTrail(13, 1);

    @Override
    public void update() {
        targetPosition.set(owner.self().aimX(), owner.self().aimY());
        rotation = Angles.moveToward(rotation, Tmp.v1.set(getPos()).angleTo(targetPosition), rotationSpeed * Time.delta);

        if(reload >= reloadTime && isShooting()){
            shootDuration = duration;
            reload = 0;
        }
        else if(shouldReload() && shootDuration <= 0) reload += Time.delta * owner.self().reloadMultiplier();

        if(shootDuration > 0){
            shootDuration -= Time.delta;
            updateLaser();
        }

        tr.trns(rotation, shootDst).add(getPos());

        laserPosition.add(Tmp.v1.trns(laserPosition.angleTo(targetPosition), Math.min(laserSpeed * Time.delta, laserPosition.dst(targetPosition)))).sub(getPos()).limit(range).add(getPos());
        trail.update(laserPosition.x, laserPosition.y);
        jtrail.update(laserPosition.x, laserPosition.y);
    }

    public void updateLaser(){
        if(shootDuration > 10 && shootDuration < duration - 10) {
            Groups.unit.intersect(laserPosition.x, laserPosition.y, 4, 4, u -> {
                u.damage(damage);
                u.apply(status, statusDuration);
                if (Mathf.chance(hitEffectChance)) hitEffect.at(laserPosition.x, laserPosition.y);
            });
            Building build = Vars.world.buildWorld(laserPosition.x, laserPosition.y);
            if(build != null) build.damage(damage);
        }
        if(Mathf.chance(passiveEffectChance)) passiveEffect.at(laserPosition.x, laserPosition.y);
    }

    @Override
    public void draw() {
        if(shootDuration < 2) return;
        tr.trns(getRotation(), shootDst).add(getPos());

        float bwidth = width * fout() + shineBaseWidth;
        float cycle = (float) (Math.sin((shineCycle * time)) * shineCycleMag);
        float swing = 1 + cycle * cycle/3;
        float alphaSpike = Mathf.clamp(fin() * 8, 0, 1) * Mathf.clamp(fout() * 4, 0, 1);

        Draw.z(Layer.bullet - 5);
        for (int i = 0; i * beamSecWidth < bwidth; i++) {
            float widthOffset = bwidth - i * beamSecWidth;
            Draw.color(frontColor, backColor, bwidth/widthOffset);
            Draw.alpha(0.65f * alphaSpike);
            Fill.quad(tr.x, tr.y, tr.x, tr.y, Tmp.v1.set(widthOffset * swing, -stumpOffset).rotate(getRotation() + 90 + swing).add(tr.x, tr.y).x, Tmp.v1.y, Tmp.v2.set(-widthOffset * swing, -stumpOffset).rotate(getRotation() + 90 + swing).add(tr.x, tr.y).x, Tmp.v2.y);
            Fill.quad(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.v3.set(laserPosition).x, Tmp.v3.y, Tmp.v3.x, Tmp.v3.y);

            Draw.blend(Blending.additive);
            Draw.color(Color.white);
            Draw.alpha(0.15f * alphaSpike);
            Fill.quad(tr.x, tr.y, tr.x, tr.y, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
            Fill.quad(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.v3.x, Tmp.v3.y, Tmp.v3.x, Tmp.v3.y);
            Draw.blend();
        }

        float shinez = (float) (Math.sin((shineCycle * time)) * shineVary) * fout();

        Draw.color(frontColor, Color.white, 0.4f);
        Fill.circle(Tmp.v3.x, Tmp.v3.y, shinez/2 * shineSize);
        Draw.color(backColor, Color.white, 0.4f);
        Fill.circle(Tmp.v3.x, Tmp.v3.y, shinez/3 * shineSize);

        Draw.reset();

        Draw.z(Layer.effect + 1);
        trail.draw(Tmp.c1.set(frontColor).a(0.35f), shinez);
        jtrail.draw(Tmp.c1.set(frontColor).a(0.35f), shinez);

        Draw.blend(Blending.additive);
        trail.draw(Tmp.c2.set(Color.white).a(0.15f), shinez);
        jtrail.draw(Tmp.c1.set(frontColor).a(0.35f), shinez);

        Fill.light(Tmp.v3.x, Tmp.v3.y, shineSides, shinez, Tmp.c2.set(frontColor).lerp(backColor, Mathf.sin(shineCycle * time)).a(0.35f), Tmp.c1.set(Color.white).a(0));
        for(int i: Mathf.signs){
            Draw.color(backColor);
            Draw.alpha(0.35f * alphaSpike);
            Drawf.tri(Tmp.v3.x, Tmp.v3.y, shinez * shineWidth, fout() * shineLength * 2, 90 + 90 * i);

            Draw.color(frontColor);
            Draw.alpha(0.15f * alphaSpike);
            Drawf.tri(Tmp.v3.x, Tmp.v3.y, shinez * shineWidth, fout() * shineLength, 90 + 90 * i);
        }
        Draw.blend();
    }

    float fout(){
        return shootDuration/duration;
    }

    float fin(){
        return 1 - shootDuration/duration;
    }
}
