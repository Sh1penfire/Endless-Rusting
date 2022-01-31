package rusting.entities.units.weapons;

import arc.util.Log;
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
    public UnitMount init(UnitMount mount) {
        super.init(mount);
        Log.info("init properly");
        if(mount instanceof BulletUnitMount){
            BulletUnitMount bulletMount = (BulletUnitMount) mount;
            bulletMount.bulletType = bulletType;
        }
        return mount;
    }
}
