package rusting.entities.units.weapons.mounts;

import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import rusting.content.RustingBullets;

public class BulletUnitMount extends ShootUnitMount{

    public BulletUnitMount(){
        super();
        name = "bullet";
    }

    //bullet in use
    public BulletType bulletType;

    @Override
    public void shoot() {
        super.shoot();
        bulletType.create(
                owner.self(),
                owner.self().team,
                Tmp.v1.set(getPos().add(Tmp.v2.set(shootX, shootY).rotate(getRotation() - 90))).x, Tmp.v1.y, getRotation());
    }

    @Override
    public boolean shouldReload() {
        return reload < reloadTime;
    }

    @Override
    public boolean canShoot() {
        return reload >= reloadTime && !owner.self().disarmed();
    }

    @Override
    public void write(Writes w) {
        super.write(w);
        w.str(bulletType.minfo.mod != null ? bulletType.minfo.mod.name : "");
        w.s(bulletType.id);
        w.s(Vars.content.bullets().copy().filter(b -> b.minfo.mod == bulletType.minfo.mod).get(0).id);
    }

    @Override
    public void readMount(Reads r, byte revision) {
        super.readMount(r, revision);

        bulletType = RustingBullets.findBullet(r.str(), r.s(), r.s());
    }
}

