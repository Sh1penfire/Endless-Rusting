package rusting.world.blocks.pulse.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.content.UnitTypes;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.blocks.ControlBlock;
import rusting.content.*;
import rusting.interfaces.Targeting;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

//note: eventually going to merge hardcoded behaviour which is intended as a replacement for scripted sectors onto another class, then give the player generators which can shoot
public class InfectedsGeneratorCore extends PulseGenerator{
    public TextureRegion heatRegion, topRegion, baseRegion, rotatorRegion, topRotatorRegion, topRotatorRegionHeat;
    public float rotatorSpeed = 3;
    public Color heatColor = Palr.pulseBullet;
    private static Rand random = new Rand(0);
    public float topRotatorSpeed = 9.5f;
    //Pulse for each shot
    public float pulseConsumedBullet = 35;
    //reload for block defense's projectiles
    public float reloadTime = 4;

    public InfectedsGeneratorCore(String name) {
        super(name);
        this.projectile = RustingBullets.bigCraeWeaver;
        projectileOffset = 9;

    }

    @Override
    public void load() {
        super.load();
        heatRegion = Core.atlas.find(name + "-heat");
        topRegion = Core.atlas.find(name + "-top");
        baseRegion = Core.atlas.find(name + "-base");
        rotatorRegion = Core.atlas.find(name + "-rotator");
        topRotatorRegion = Core.atlas.find(name + "-rotator-top");;
        topRotatorRegionHeat = Core.atlas.find(name + "-rotator-top-heat");
    }

    @Override
    public boolean hidden() {
        return true;
    }

    public class InfectedsGeneratorCoreBuild extends PulseGeneratorBuild implements ControlBlock, Targeting{

        public float totalProgress = 0;
        public float warmup = 0;
        public float topRotorRotation = 0;
        public float shootWarmup = 0;
        public float lastShootRot = 0;
        Posc target = null;
        public float progressReload = 0;
        //reload for main bullet
        public float reload = 0;
        public boolean shootingMode = false;

        public Vec2 targetPos = new Vec2();

        public @Nullable
        BlockUnitc unit;

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if(unit != null){
                unit.health(health);
                unit.rotation(rotation);
                unit.team(team);
                unit.set(x, y);
            }
            totalProgress += pEfficiency() * Time.delta * warmup;
            if(allConsValid()) {
                warmup = Mathf.approachDelta(warmup, 1, pEfficiency() * Time.delta / 100);
            }
            else warmup = Mathf.approachDelta(warmup, 0, pEfficiency() * Time.delta / 100);
            if(overloaded() && isShooting()) shootWarmup = Mathf.approachDelta(shootWarmup, 1, pEfficiency() * Time.delta / 840);
            else shootWarmup = Mathf.approachDelta(shootWarmup, 0, pEfficiency() * Time.delta / 150);
            if(canShoot()) target = Units.closestTarget(team, x, y, projectileRange());
            if(isShooting()) {
                updateTargetPos();
            }
            topRotorRotation += pEfficiency() * Time.delta * topRotatorSpeed * shootWarmup;
        }

        public boolean canShoot(){
            return overloaded() && pulseAmount >= pulseConsumedBullet;
        }

        public boolean isShooting(){
            return isControlled() ? unit().isShooting : target != null;
        }

        public void updateTargetPos(){
            if(isControlled()) targetPos.set(unit.aimX(), unit.aimY());
            else if(target != null) targetPos.set(target.x(), target.y());
        }

        @Override
        public Vec2 targetPos() {
            return targetPos;
        }

        @Override
        public void overloadEffect() {
            float inaccuracy = 10 + 250 * (1 - shootWarmup)/2;
            if(canShoot() && isShooting() && shootingMode){
                lastShootRot = angleTo(targetPos);
                float minShootWarmup = Math.max(shootWarmup * 2, 0.55f);
                if(reload >= reloadTime) {
                    reload %= reloadTime;
                    removePulse(pulseConsumedBullet);
                    float angle = lastShootRot - inaccuracy / 2 + Mathf.random(inaccuracy);
                    Tmp.v1.trns(angle, projectileOffset);
                    projectile.create(this, team, x + Tmp.v1.x, y + Tmp.v1.y, angle, 0.8f * size * minShootWarmup, 1/minShootWarmup);
                }
                else reload += pEfficiency() * Time.delta * minShootWarmup;
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);
            table.button(Tex.checkOn, () -> {
                shootingMode = !shootingMode;
            });
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            if(team != state.rules.waveTeam && state.rules.attackMode) return;
            float range = 250;
            if(state.rules.damageExplosions) Damage.damage(x, y, tilesize * block.size * range/8f / 2.0f, 146f);
            float tx = x, ty = y;
            for (int i = 0; i < 8; i++) {
                Time.run(i * 4 + Mathf.random(3), () -> {
                    Tmp.v1.trns(Mathf.random(360), Mathf.random(35));
                    RustingBullets.boltingVortex.create(this, tx + Tmp.v1.x, ty + Tmp.v1.y, Tmp.v1.angle());
                });
            }
            for (int i = 0; i < 13; i++) {
                Time.run(i * 7 + Mathf.random(3), () -> {
                    Tmp.v1.trns(Mathf.random(360), Mathf.random(35));
                    Fxr.pulseSmoke.at(tx + Tmp.v1.x, ty + Tmp.v1.y, 0, new Float[]{range * 45, 5f});
                });
            }
            RustingBullets.infectedGeneratorCoreNuke.create(null, null, x, y, 0f, Float.MAX_VALUE, 0f, 1f, null);
            Fxr.pulseSmoke.at(x, y, 0, new Float[]{range * 8, 5f});
            Fxr.spontaniumCOMBUSTOMTHATSTHESPELLWHICHMAKESANYONEWHOSAYSITEXPLO.at(x, y, Mathf.random(0, 360));
            ModSounds.zapHum.at(x, y, 0.35f, 100);
        }

        @Override
        public void draw() {
            Draw.reset();
            Draw.rect(baseRegion, x, y);
            if(pulseRegion != Core.atlas.find("error")) {
                Draw.color(chargeColourStart, chargeColourEnd, chargef());
                Draw.alpha(chargef()/0.35f);
                Draw.rect(pulseRegion, x, y);
                Draw.reset();
                Draw.z(Layer.block);
            }

            Draw.rect(rotatorRegion, x, y, totalProgress * rotatorSpeed % 360);

            Draw.z(Layer.blockOver);
            Draw.rect(topRegion, x, y);

            if(heatRegion != Core.atlas.find("error")) {
                Draw.color(heatColor);
                Draw.alpha(Math.min(Mathf.absin(totalProgress, 1, 1), 0.25f * warmup));
                Draw.rect(heatRegion, x, y, 270);
                if(Core.settings.getBool("settings.er.pulseglare")){
                    for(int i = 1; i < 6; i++) {
                        Draw.alpha(Math.min(Mathf.absin(totalProgress, 1, 1/i), 0.25f * warmup));
                        Draw.rect(heatRegion, x, y, pulseRegion.height * (1 + i/6 )/ 4, pulseRegion.width * (1 + i/6 )/ 4, 270);
                    }
                }
            }

            Draw.reset();
            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRotatorRegion, x, y, topRotorRotation % 360);

            Draw.reset();

            if(shootWarmup > 0.01f){
                Draw.z(Layer.effect);
                Draw.color(chargeColourStart, chargeColourEnd, Mathf.absin(Time.delta/90, 1, 1));
                Draw.alpha(shootWarmup);
                Draw.rect(topRotatorRegionHeat, x, y, topRotorRotation % 360);
                for (int i = 1; i < 6; i++) {
                    Draw.color(Palr.pulseChargeStart, Palr.pulseBullet, shootWarmup);
                    Draw.alpha(shootWarmup);
                    Lines.stroke(shootWarmup/i * 3);
                    Lines.swirl(x, y, 20 - 2 * i, 86/360, lastShootRot - 86/2);
                }
            }
        }
    }
}
