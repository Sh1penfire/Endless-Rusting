package rusting.entities.units.weapons;

import mindustry.entities.bullet.BulletType;
import rusting.content.RustingBullets;
import rusting.entities.units.weapons.mounts.*;

public class BulletMountType extends ShootMountType {
    public BulletType bulletType = RustingBullets.ddd;

    public BulletMountType(String name) {
        super(name);
        mountType = BulletUnitMount::new;
    }

    @Override
    public void init(UnitMount mount) {
        super.init(mount);
        if(mount instanceof BulletUnitMount){
            BulletUnitMount bulletMount = (BulletUnitMount) mount;
            bulletMount.bulletType = bulletType;
        }
    }
}
