package rusting.entities.units.weapons;

import arc.math.Angles;
import arc.util.Time;
import arc.util.Tmp;

public class SpecialShootWeapon extends SpecialWeapon{
    public SpecialShootWeapon(String name) {
        super(name);
    }

    public float reload = 85;

    //the offset from the weapon that the attack starts from
    public float shootx = 0, shooty = 0;

    @Override
    public void update(SpecialWeaponMount mount){
        super.update(mount);
        if(rotates) mount.rotation = Angles.moveToward(Tmp.v1.set(mount.getPos()).angleTo(mount.owner.self().aimX, mount.owner.self().aimY), mount.rotation, rotateSpeed * Time.delta);

        if(isShooting(mount) && shouldReload(mount)) {
            mount.reload += Time.delta * mount.owner.self().reloadMultiplier();
            if (mount.reload >= reload) shoot(mount);
        }
    }

    public void shoot(SpecialWeaponMount mount){
        mount.reload = 0;
    }

    public boolean shouldReload(SpecialWeaponMount mount){
        return true;
    }

    public boolean canShoot(SpecialWeaponMount mount){
        return true;
    }

    public boolean isShooting(SpecialWeaponMount mount){
        return mount.owner.self().isShooting();
    }
}
