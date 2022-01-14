package rusting.entities.units.weapons;

import rusting.entities.units.weapons.mounts.PointLaserUnitMount;

public class PointLaserMountType extends ShootMountType{
    public PointLaserMountType(String name) {
        super(name);
        mountType = PointLaserUnitMount::new;
    }
}
