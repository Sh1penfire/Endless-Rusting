package rusting.world.blocks.pulse.defense;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.ControlBlock;
import rusting.content.Fxr;
import rusting.content.Palr;
import rusting.graphics.*;
import rusting.interfaces.*;
import rusting.interfaces.block.PulseCanalc;
import rusting.world.blocks.pulse.PulseBlock;

import static arc.util.Time.time;

public class PulsePreciseLaserTurret extends PulseBlock {

    private static float bulletLen = 0;

    public float range = 230;

    public float reloadTime = 180;
    public float rotateSpeed = 1;

    public float duration = 360;

    public float shootDst = 4.5f;

    public float armorPiercingFactor = 0.15f;

    public float width = 3, shineBaseWidth = 6, length = 45;
    public float stumpOffset = 8;
    public float laserSpeed = 1.5f;

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

    public TextureRegion baseRegion, heatRegion;

    public static Vec2 tr = new Vec2();

    public PulsePreciseLaserTurret(String name) {
        super(name);
        rotate = false;
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
        heatRegion = Core.atlas.find(name + "-heat");
    }

    public class PulsePreciseLaserTurretBuild extends PulseBlockBuild implements ControlBlock, Targeting, PulseCanalc {
        public float rotation = 90;

        //laser position is it's own thing
        public Vec2 laserPosition = new Vec2(x, y), targetPosition = new Vec2(x, y);

        //shoot duration is how long the turret has left to shoot, charge is the progress to being fully charged and the reload is the normal reload value
        public float shootDuration = 0, charge = 0, reload = 0;

        public boolean isCharging = false, shootingLaser = false;

        public PoolableTrail trail = new PoolableTrail(25);
        public JagedTrail jtrail = new JagedTrail(13, 1);

        public @Nullable
        BlockUnitc unit;

        public @Nullable
        Posc target;

        @Override
        public void updateTile() {
            super.updateTile();
            findTarget();
            if(isShooting()) rotation = Angles.moveToward(rotation, angleTo(targetPosition), rotateSpeed * Time.delta);

            if(isShooting() && reload < reloadTime && shootDuration <= 0) reload += Time.delta;
            else if(reload >= reloadTime){
                shootDuration = duration;
                reload = 0;
            }

            if(shootDuration > 0){
                shootDuration -= Time.delta;
                updateLaser();
            }

            tr.trns(rotation, shootDst).add(this.x, this.y);

            if(isControlled()) targetPosition.set(unit().aimX(), unit().aimY());
            else if(target != null) targetPosition.set(target.x(), target.y());
            laserPosition.add(Tmp.v1.trns(laserPosition.angleTo(targetPosition), Math.min(laserSpeed * Time.delta, laserPosition.dst(targetPosition)))).sub(x, y).limit(range).add(x, y);
            trail.update(laserPosition.x, laserPosition.y);
            jtrail.update(laserPosition.x, laserPosition.y);
        }

        public void updateLaser(){
            Groups.unit.intersect(laserPosition.x, laserPosition.y, 4, 4, u -> {
                u.health -= damage * armorPiercingFactor * Time.delta;
                u.damage(damage);
                u.apply(status, statusDuration);
                if(Mathf.chance(hitEffectChance)) hitEffect.at(laserPosition.x, laserPosition.y);
            });
            if(Mathf.chance(passiveEffectChance)) passiveEffect.at(laserPosition.x, laserPosition.y);
        }

        public boolean isShooting(){
            return !isCharging && (target != null && !isControlled() || unit().isShooting());
        }

        void findTarget(){
            target = Units.closestEnemy(team, x, y, range(), u -> this.damaged() || !(u instanceof Pulsec));
        }

        @Override
        public float range() {
            return range;
        }

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }

        @Override
        public boolean isControlled() {
            return unit().controller() instanceof Player;
        }

        @Override
        public boolean canConnect(Building b) {
            return true;
        }

        @Override
        public boolean canReceive(Building b) {
            return true;
        }

        //the fact that this is probably one of the best draw codes I've made says something and I'm scared about future implications
        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.block);
            Draw.rect(baseRegion, x, y, 0);
            Draw.z(Layer.turret);
            Draw.rect(region, x, y, rotation - 90);
            tr.trns(rotation, shootDst).add(this.x, this.y);

            float bwidth = width * fout() + shineBaseWidth;
            float cycle = (float) (Math.sin((shineCycle * time)) * shineCycleMag);
            float swing = 1 + cycle * cycle/3;
            float alphaSpike = Mathf.clamp(fin() * 8, 0, 1) * Mathf.clamp(fout() * 4, 0, 1);

            Draw.z(Layer.effect + 1);
            for (int i = 0; i * beamSecWidth < bwidth; i++) {
                float widthOffset = bwidth - i * beamSecWidth;
                Draw.color(frontColor, backColor, bwidth/widthOffset);
                Draw.alpha(0.65f * alphaSpike);
                Fill.quad(tr.x, tr.y, tr.x, tr.y, Tmp.v1.set(widthOffset * swing, -stumpOffset).rotate(rotation + 90 + swing).add(tr.x, tr.y).x, Tmp.v1.y, Tmp.v2.set(-widthOffset * swing, -stumpOffset).rotate(rotation + 90 + swing).add(tr.x, tr.y).x, Tmp.v2.y);
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
}
