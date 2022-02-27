package rusting.entities.units.weapons;

import arc.audio.Sound;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import rusting.content.RustingBullets;

public class BulletMountType extends ShootMountType {

    public float shake = 0, shakeDuration = 60;
    public int shots = 1;
    public float spacing = 0, inaccuracy = 0, velocityRand = 0.15f;
    public BulletType bulletType = RustingBullets.ddd;
    public Sound shootSound = Sounds.bang;

    public BulletMountType(String name) {
        super(name);
        mountType = BulletUnitMount::new;
    }

    public class BulletUnitMount extends ShootUnitMount{

        public BulletUnitMount(){
            super();
            name = "bullet";
        }

        @Override
        public void update() {
            super.update();
            recoil = Math.max(recoil - type.restitution * Time.delta, 0);
        }

        @Override
        public void shoot() {
            reload = 0;
            bullet(getRotation());
            owner.self().vel.add(Tmp.v1.trns(getRotation() + 180.0F, bulletType.recoil));
            effects(bulletType);
        }

        public Bullet bullet(float angle){
            return bulletType.create(
                    owner.self(),
                    owner.self().team,
                    Tmp.v1.set(getShootPos()).x, getShootPos().y, angle);
        }

        public void effects(BulletType type){
            Vec2 shootPos = new Vec2(getPos());
            shootPos.add(Tmp.v1.set(shootX, shootY).rotate(getRotation() - 90));
            shootSound.at(getPos().x, getPos().y, 1, 1);
            type.shootEffect.at(shootPos.x, shootPos.y, getRotation());
            recoil = 1;
        }

        @Override
        public boolean shouldReload() {
            return reload < reloadTime;
        }

        @Override
        public boolean canShoot() {
            return reload >= reloadTime && !owner.self().disarmed();
        }
    }
}
