package rusting.entities.units.weapons;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import rusting.interfaces.SpecialWeaponsUnit;

public class ShotgunMountType extends BulletMountType{
    public BulletType critBullet;
    public Effect shellLoad = Fx.casing1;
    public Effect critShootEffect = Fx.explosion;
    public float critSpeedMulti = 1.35f;
    public float shellReloadTime = 60;
    public int critShots = 3;
    public float shellSpace = 3;
    public float critDamageMulti = 2.25f, critLifetimeMulti = 1.35f;
    public float critShakeMulti = 3;

    public ShotgunMountType(String name) {
        super(name);
        reload = 15;
        mountType = ShotgunUnitMount::new;
    }

    @Override
    public void update(SpecialWeaponsUnit unit, UnitMount mount) {
        ShotgunUnitMount shoot = (ShotgunUnitMount) mount;
        shoot.rotation = Angles.moveToward(shoot.rotation, shoot.getPos().angleTo(unit.self().aimX, unit.self().aimY), rotateSpeed * Time.delta);

        if(shoot.shouldReload() && shoot.shells < shellSpace && (!shoot.isShooting() || shoot.shells <= 1)) {
            shoot.shellReload += Time.delta * unit.self().reloadMultiplier();
            if(shoot.shellReload >= shellReloadTime){
                shoot.shells++;
                shoot.shellReload = 0;
                shoot.consecutive = 0;
                shellLoad.at(shoot.getPos().x, shoot.getPos().y);
            }
        }

        if (shoot.isShooting()) {
            shoot.reload += Time.delta * unit.self().reloadMultiplier();
            if(shoot.reload >= reload && shoot.shells > 0) {
                shoot.shoot();
            }
        }
    }

    public class ShotgunUnitMount extends BulletUnitMount{
        public int shells = 0, shellReload = 0;
        public int consecutive = 0;
        public boolean crit = false;

        @Override
        public void shoot() {
            ShotgunMountType shotgun = (ShotgunMountType) type;

            consecutive++;
            if(consecutive >= shellSpace) crit = true;
            Tmp.v1.set(getPos().add(Tmp.v2.set(shootX, shootY).rotate(getRotation() - 90)));
            BulletType shot = crit && critBullet != null ? critBullet : bullet;
            Angles.shotgun(crit ? critShots : shots, spacing, getRotation(), f -> {
                Bullet b = shot.create(
                        owner.self(),
                        owner.self().team,
                        Tmp.v1.x, Tmp.v1.y, f + Mathf.range(inaccuracy),
                        1 + Mathf.random(velocityRand), 1);

                if(crit){
                    consecutive = 0;
                    b.damage *= shotgun.critDamageMulti;
                    b.lifetime *= critLifetimeMulti;
                    b.vel.scl(critSpeedMulti, critSpeedMulti);
                }
            });
            shells -= 1;
            reload = 0;
            effects(bullet);
            crit = false;
        }

        @Override
        public void effects(BulletType type){
            Vec2 shootPos = new Vec2(getPos());
            shootPos.add(Tmp.v1.set(shootX, shootY).rotate(getRotation()));
            shootSound.at(getPos().x, getPos().y, 1, 1);
            if(crit) critShootEffect.at(shootPos.x, shootPos.y, getRotation());
            else type.shootEffect.at(shootPos.x, shootPos.y, getRotation());
            Effect.shake((crit ? shake * critShakeMulti : shake), shakeDuration, shootPos.x, shootPos.y);
        }

        @Override
        public boolean shouldReload() {
            return shells < ((ShotgunMountType) type).shellSpace;
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.i(shells);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            shells = r.i();
        }
    }
}
