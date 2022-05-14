package rusting.entities.bullet;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.content.StatusEffects;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.type.StatusEffect;
import rusting.content.Palr;
import rusting.world.blocks.defense.turret.HarpoonTurret.HarpoonTurretBuild;

//a special class for bullets which are used in the harpoon gun
public class BlockHarpoonBulletType extends BaseBulletType {

    public float shredDamage = 0;
    //chance per tick
    public float dischargeChance = 0.015f;

    //damage every time the unit's damaged from harpoon
    public float ripDamage = 5;
    //damage when the harpoon is riped out
    public float tearDamage = 65;
    //if the harpoon should spawn lightning when lodged into a target.
    public boolean dischargeLightning = true;

    //effect applied to a unit that it's stuck in every time it's damaged
    public StatusEffect bleedEffect = StatusEffects.none;
    //the duration of the applied effect
    public float bleedEffectDuration = 60;

    //region for drawing the chain
    public TextureRegion chainRegion;

    public BlockHarpoonBulletType(float speed, float damage, String sprite) {
        super(speed, damage, sprite);
        this.shrinkX = 0;
        this.shrinkY = 0;
        backColor = Color.white;
        frontColor = Color.white;
        lightningDamage = 3;
        lightningLength = 5;
        lightningColor = Palr.camaintLightning;
        reflectable = false;
        absorbable = false;
        hittable = false;
    }

    @Override
    public void load() {
        super.load();
        chainRegion = Core.atlas.find(sprite + "-chain");
    }

    @Override
    public void draw(Bullet b) {
        return;
    }

    @Override
    public void despawned(Bullet b) {
        resetHarpoon(b);
        super.despawned(b);
    }

    public void updateUnitEffect(HarpoonTurretBuild build, Unit unit){
        if(dischargeLightning && Mathf.randomBoolean(dischargeChance)) Lightning.create(build.team, lightningColor, lightningDamage < 0 ? damage : lightningDamage, unit.x, unit.y, unit.rotation() + Mathf.range(lightningCone/2) + lightningAngle, lightningLength + Mathf.random(lightningLengthRand));
    }

    //retract harpoon
    public void resetHarpoon(Bullet b){
        if(b.owner instanceof HarpoonTurretBuild) {
            HarpoonTurretBuild turret = ((HarpoonTurretBuild) b.owner);
            turret.harpoonRetracting = true;
        }
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc other, float initialHealth) {
        super.hitEntity(b, other, initialHealth);

        Unit u = Units.closestEnemy(b.team, b.x, b.y, hitSize + 8, unit -> unit != null);
        if(u != null) b.collided.add(u.id);
        if(b.owner instanceof HarpoonTurretBuild && b.collided.size > 0) {
            HarpoonTurretBuild harpoon = ((HarpoonTurretBuild) b.owner);
            harpoon.harpoonStuck = true;
            harpoon.harpoonRetracting = false;
            harpoon.stuckOn = Groups.unit.getByID(b.collided.get(b.collided.size - 1));
            harpoon.relativeRotation = harpoon.stuckOn.angleTo(b.x, b.y) - harpoon.stuckOn.rotation;
            harpoon.distanceOffset = b.dst(harpoon.stuckOn);
        }
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        resetHarpoon(b);
    }
}

