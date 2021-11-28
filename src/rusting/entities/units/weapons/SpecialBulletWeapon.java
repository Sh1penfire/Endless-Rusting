package rusting.entities.units.weapons;

import arc.util.Tmp;
import mindustry.entities.bullet.BulletType;
import rusting.content.RustingBullets;

public class SpecialBulletWeapon extends SpecialShootWeapon{
    public BulletType bulletType = RustingBullets.ddd;

    public SpecialBulletWeapon(String name) {
        super(name);
    }

    @Override
    public void shoot(SpecialWeaponMount mount) {
        super.shoot(mount);
        bulletType.create(mount.owner.self(), mount.owner.self().team, Tmp.v1.set(mount.getPos()).add(Tmp.v2.set(shootx, shooty).rotate(mount.getRotation() - 90)).x, Tmp.v1.y, mount.getRotation());
    }
}
