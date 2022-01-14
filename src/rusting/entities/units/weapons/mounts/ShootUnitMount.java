package rusting.entities.units.weapons.mounts;


import arc.math.Angles;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;

public class ShootUnitMount extends UnitMount{

    public ShootUnitMount(){
        super();
        name = "shoot";
    }
    
    public float reloadTime;
    public float shootX, shootY;

    @Override
    public void update() {
        rotation = Angles.moveToward(Tmp.v1.set(getPos()).angleTo(owner.self().aimX, owner.self().aimY), rotation, rotationSpeed/60 * Time.delta);

        if(isShooting() && shouldReload()) {
            reload += Time.delta * owner.self().reloadMultiplier();
            if (canShoot()) shoot();
        }
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

    @Override
    public void write(Writes w) {
        super.write(w);
        w.f(reloadTime);
        w.f(shootX);
        w.f(shootY);
    }

    @Override
    public void readMount(Reads r, byte revision) {
        super.readMount(r, revision);
        reloadTime = r.f();
        shootX = r.f();
        shootY = r.f();
    }
}
