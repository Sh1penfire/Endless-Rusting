package rusting.type.weapons;

import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.type.Weapon;

import static mindustry.Vars.net;
import static mindustry.Vars.state;

public class MeleeWeapon extends Weapon {
    //damage it does
    public float damage;
    //knockback the weapon has
    public float knockback;
    //angle in which the weapon arcs in
    public float arc;
    public float recoil;
    public float weaponRecoil;
    //should be false by default, though if you're doing something gimiky then go ahead and enable
    public boolean useAmmo = false;

    public void update(Unit unit, WeaponMount mount){
        boolean can = unit.canShoot();
        mount.reload = Math.max(mount.reload - Time.delta, 0);

        //some ctrlc n ved code from weapon, conveniently there
        float
        weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : 0),
        mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
        mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
        shootX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
        shootY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
        shootAngle = rotate ? weaponRotation + 90 : Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));

        boolean shoot = false;

        mount.shoot = mount.rotate = shoot;

        //heat decreases when not firing
        mount.heat = Math.max(mount.heat - Time.delta / mount.weapon.cooldownTime, 0);

        if(mount.sound != null){
            mount.sound.update(shootX, shootY, false);
        }

        //flip weapon shoot side for alternating weapons at half reload
        if(otherSide != -1 && alternate && mount.side == flipSprite &&
        mount.reload + Time.delta > reload/2f && mount.reload <= reload/2f){
            unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
            mount.side = !mount.side;
        }

        //rotate if applicable
        if(rotate && (mount.rotate || mount.shoot) && can){
            float axisX = unit.x + Angles.trnsx(unit.rotation - 90,  x, y),
                    axisY = unit.y + Angles.trnsy(unit.rotation - 90,  x, y);

            mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
        }else if(!rotate){
            mount.rotation = 0;
            mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
        }

        //shoot if applicable
        if(mount.shoot && //must be shooting
                can && //must be able to shoot
                (!useAmmo || unit.ammo > 0 || !state.rules.unitAmmo || unit.team.rules().infiniteAmmo) && //check ammo
                (!alternate || mount.side == flipSprite) &&
                //TODO checking for velocity this way isn't entirely correct
                (unit.vel.len() >= mount.weapon.minShootVelocity || (net.active() && !unit.isLocal())) && //check velocity requirements
                mount.reload <= 0.0001f && //reload has to be 0
                Angles.within(rotate ? mount.rotation : unit.rotation, mount.targetRotation, mount.weapon.shootCone) //has to be within the cone
        ){
            shoot(unit, mount, shootX, shootY, mount.aimX, mount.aimY, mountX, mountY, shootAngle, Mathf.sign(x));

            mount.reload = reload;

            if(useAmmo){
                unit.ammo--;
                if(unit.ammo < 0) unit.ammo = 0;
            }
        }
    }

    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side){
        float baseX = unit.x, baseY = unit.y;
        boolean delay = firstShotDelay + shotDelay > 0f;

        (delay ? chargeSound : continuous ? Sounds.none : shootSound).at(shootX, shootY, Mathf.random(soundPitchMin, soundPitchMax));

        BulletType ammo = bullet;
        float lifeScl = ammo.scaleVelocity ? Mathf.clamp(Mathf.dst(shootX, shootY, aimX, aimY) / ammo.range()) : 1f;

        float[] sequenceNum = {0};
        if(delay){
            Angles.shotgun(shots, spacing, rotation, f -> {
                Time.run(sequenceNum[0] * shotDelay + firstShotDelay, () -> {
                    if(!unit.isAdded()) return;
                    mount.bullet = bullet(unit, shootX + unit.x - baseX, shootY + unit.y - baseY, f + Mathf.range(inaccuracy), lifeScl);
                });
                sequenceNum[0]++;
            });
        }else{
            Angles.shotgun(shots, spacing, rotation, f -> mount.bullet = bullet(unit, shootX, shootY, f + Mathf.range(inaccuracy), lifeScl));
        }

        boolean parentize = ammo.keepVelocity;

        if(delay){
            Time.run(firstShotDelay, () -> {
                if(!unit.isAdded()) return;

                unit.vel.add(Tmp.v1.trns(rotation + 180f, ammo.recoil));
                Effect.shake(shake, shake, shootX, shootY);
                mount.heat = 1f;
                if(!continuous){
                    shootSound.at(shootX, shootY, Mathf.random(soundPitchMin, soundPitchMax));
                }
            });
        }else{
            unit.vel.add(Tmp.v1.trns(rotation + 180f, ammo.recoil));
            Effect.shake(shake, shake, shootX, shootY);
            mount.heat = 1f;
        }

        ejectEffect.at(mountX, mountY, rotation * side);
        ammo.shootEffect.at(shootX, shootY, rotation, parentize ? unit : null);
        ammo.smokeEffect.at(shootX, shootY, rotation, parentize ? unit : null);
        unit.apply(shootStatus, shootStatusDuration);
    }

    protected Bullet bullet(Unit unit, float shootX, float shootY, float angle, float lifescl){
        float xr = Mathf.range(xRand);

        return bullet.create(unit, unit.team,
        shootX + Angles.trnsx(angle, 0, xr),
        shootY + Angles.trnsy(angle, 0, xr),
        angle, (1f - velocityRnd) + Mathf.random(velocityRnd), lifescl);
    }

}
