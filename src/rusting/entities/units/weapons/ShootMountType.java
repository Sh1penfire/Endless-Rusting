package rusting.entities.units.weapons;

import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import rusting.interfaces.SpecialWeaponsUnit;

public class ShootMountType extends MountType {

    public ShootMountType(String name) {
        super(name);
        mountType = ShootUnitMount::new;
    }

    public float reloadTime = 85;

    //the offset from the weapon that the attack starts from
    public float shootX = 0, shootY = 0;

    @Override
    public UnitMount init(UnitMount mount) {
        super.init(mount);
        if(mount instanceof ShootUnitMount){
            ShootUnitMount shoot = (ShootUnitMount) mount;
            shoot.reload = 0;
        }
        return mount;
    }

    @Override
    public void update(SpecialWeaponsUnit unit, UnitMount mount) {
        ShootUnitMount shoot = (ShootUnitMount) mount;
        shoot.rotation = Angles.moveToward(shoot.rotation, shoot.getPos().angleTo(unit.self().aimX, unit.self().aimY), rotateSpeed * Time.delta);

        if(shoot.isShooting() && shoot.shouldReload()) {
            shoot.reload += Time.delta * unit.self().reloadMultiplier();
            if (shoot.canShoot()) shoot.shoot();
        }
    }

    public class ShootUnitMount extends UnitMount{

        public ShootUnitMount(){
            super();
            name = "shoot";
        }

        public void shoot(){
            reload = 0;
        }

        public boolean shouldReload(){
            return reload < reloadTime;
        }

        public boolean canShoot(){
            return true;
        }

        public boolean isShooting(){
            return owner.self().isShooting();
        }

        public Vec2 getShootPos(){
            return new Vec2(x, y).rotate(owner.self().rotation - 90).add(owner.self().x, owner.self().y).add(new Vec2(shootX, shootY).rotate(getRotation()));
        }

        @Override
        public void write(Writes w) {
            super.write(w);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
        }
    }
}
