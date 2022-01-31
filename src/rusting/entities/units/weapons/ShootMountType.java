package rusting.entities.units.weapons;

import rusting.entities.units.weapons.mounts.ShootUnitMount;
import rusting.entities.units.weapons.mounts.UnitMount;

public class ShootMountType extends MountType {

    public ShootMountType(String name) {
        super(name);
        mountType = ShootUnitMount::new;
    }

    public float reload = 85;

    //the offset from the weapon that the attack starts from
    public float shootX = 0, shootY = 0;

    @Override
    public UnitMount init(UnitMount mount) {
        super.init(mount);
        if(mount instanceof ShootUnitMount){
            ShootUnitMount shoot = (ShootUnitMount) mount;
            shoot.reloadTime = reload;
            shoot.shootX = shootX;
            shoot.shootY = shootY;
        }
        return mount;
    }
}
