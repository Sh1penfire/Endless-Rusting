package rusting.entities.units.weapons;

import arc.audio.Sound;
import arc.math.geom.Vec2;
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
        public void shoot() {
            reload = 0;
            bullet(getRotation());
            effects(bulletType);
        }

        public Bullet bullet(float angle){
            return bulletType.create(
                    owner.self(),
                    owner.self().team,
                    Tmp.v1.set(getShootPos()).x, Tmp.v1.y, angle);
        }

        public void effects(BulletType type){
            Vec2 shootPos = new Vec2(getPos());
            shootPos.add(Tmp.v1.set(shootX, shootY).rotate(getRotation()));
            shootSound.at(getPos().x, getPos().y, 1, 1);
            type.shootEffect.at(shootPos.x, shootPos.y, getRotation());
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
