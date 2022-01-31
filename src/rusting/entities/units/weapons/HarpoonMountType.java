package rusting.entities.units.weapons;

import arc.func.Cons;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.gen.*;
import rusting.entities.units.weapons.mounts.UnitMount;

public class HarpoonMountType extends BulletMountType {

    private static HarpoonDataHolder currentHarpoon = null;

    public HarpoonMountType(String name) {
        super(name);
    }

    public static class HarpoonDataHolder{

        public Vec2 harpoonPosition = new Vec2(0, 0);
        public Bullet harpoonBullet;
        public boolean harpoonShot = false;
        public boolean harpoonStuck = false;
        public boolean harpoonRetracting = false;
        public float relativeRotation = 0;
        public float distanceOffset = 0;
        public Unit stuckOn;
        public int harpoonAmmo = 0;
        public float lastBulletLifetime = 0;

        //damage units every 10 ticks
        public float damageInterval = 0;
        Seq<Cons> consSeq = Seq.with();

    }

    /*
    @Override
    public void update(UnitMount mount) {
        super.update(mount);
        if(harpoonRetracting){
            harpoonPosition.add(Tmp.v2.trns(Tmp.v1.set(harpoonPosition).angleTo(this.x, this.y), Mathf.clamp(dst(harpoonPosition), 0, retractSpeed * timeScale())));
            if(within(harpoonPosition, shootLength + 1)){
                harpoonRetracting = false;
                harpoonShot = false;
                reload = reloadTime * autoreloadThreshold;
                Sounds.click.at(x, y);
            }
        }
        else if(harpoonShot && harpoonBullet != null && !harpoonStuck && !harpoonRetracting){
            harpoonPosition.x = harpoonBullet.x;
            harpoonPosition.y = harpoonBullet.y;
            if(harpoonBullet.lifetime != 0) lastBulletLifetime = harpoonBullet.fout();
            rotation = angleTo(harpoonBullet);
        }
        else if(harpoonStuck){
            if(stuckOn == null || stuckOn.dead == true) {
                harpoonStuck = false;
                harpoonShot = false;
                harpoonRetracting = true;
            }
            else {
                harpoonPosition.set(stuckOn.x, stuckOn.y).add(Tmp.v2.trns(stuckOn.rotation + relativeRotation, distanceOffset));

                tr.trns(rotation, shootLength);

                if(Tmp.v1.set(stuckOn.x, stuckOn.y).add(stuckOn.vel).dst(x, y) > range){
                    harpoonStuck = false;
                    harpoonShot = false;
                    harpoonRetracting = false;
                    if(hasHarpoon()) {
                        BlockHarpoonBulletType bullet = getHarpoon();
                        stuckOn.damagePierce(bullet.tearDamage);
                        stuckOn.apply(bullet.bleedEffect, bullet.bleedEffectDuration);
                        Fxr.instaltSummonerExplosion.at(harpoonPosition.x, harpoonPosition.y);
                        Geometry.iterateLine(0, x, y, stuckOn.x, stuckOn.y, getHarpoon().chainRegion.height/4, (x2, y2) -> {
                            Fxr.regionDrop.at(x2, y2, angleTo(harpoonPosition) - 90, getHarpoon().chainRegion);
                        });
                    };
                    stuckOn = null;

                }
                else {
                    if(isShooting()) {
                        stuckOn.impulse(Tmp.v2.trns(Tmp.v1.set(stuckOn.x, stuckOn.y).angleTo(x + tr.x, y + tr.y), Mathf.lerp(pullStrength, basePullStrength, Mathf.clamp(1 - (dst(stuckOn) + detachRange) / (range - detachRange), 0, 1)) * Time.delta));
                        if(hasHarpoon()) {
                            BlockHarpoonBulletType bullet = getHarpoon();
                            bullet.updateUnitEffect(this, stuckOn);

                            if (damageInterval >= 10) {
                                stuckOn.damagePierce(bullet.ripDamage * Mathf.clamp((dst(stuckOn) - detachRange) / (range - detachRange), 0, 1));
                                damageInterval -= 10;
                            } else damageInterval += Time.delta;
                        }
                    }
                }
                float toHarpoon = angleTo(harpoonPosition.x, harpoonPosition.y);
                rotation = Mathf.clamp(Angles.moveToward(rotation, toHarpoon, 1f * Time.delta),toHarpoon - 15, toHarpoon + 15);
            }
        }
    }

    @Override
    public boolean shouldReload(UnitMount mount) {
        return mount.reload <= autoreloadThreshold * reload && !isShooting() && !harpoonShot && !harpoonStuck;
    }

    */

    public static HarpoonDataHolder getHarpoonHolder(UnitMount mount){
        return mount.data instanceof HarpoonDataHolder ? (HarpoonDataHolder) mount.data : setupHarpoon(mount);
    }

    public static HarpoonDataHolder setupHarpoon(UnitMount mount){
        HarpoonDataHolder harpoon = new HarpoonDataHolder();
        mount.data = harpoon;
        return harpoon;
    }


}
