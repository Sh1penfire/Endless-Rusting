package rusting.entities.units.weapons;

import arc.func.Cons;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

public class SpecialHarpoonWeapon extends SpecialBulletWeapon{

    private static HarpoonDataHolder currentHarpoon = null;

    public SpecialHarpoonWeapon(String name) {
        super(name);
    }

    public static class HarpoonDataHolder{

        public Vec2 harpoonPosition = new Vec2(0, 0);
        public Bullet harpoonBullet;
        public boolean harpoonShot = false;
        public boolean harpoonStuck = false;
        public boolean harpoonRetracting = false;
        public float relativeRotation = 0;
        public float distanceOffset = 0;
        public Unit stuckOn;
        public int harpoonAmmo = 0;
        public float lastBulletLifetime = 0;

        //damage units every 10 ticks
        public float damageInterval = 0;
        Seq<Cons> consSeq = Seq.with();

    }

    @Override
    public void shoot(SpecialWeaponMount mount) {
        currentHarpoon = getHarpoonHolder(mount);

    }

    @Override
    public void init(SpecialWeaponMount mount) {
        super.init(mount);
        setupHarpoon(mount);
    }

    public static HarpoonDataHolder getHarpoonHolder(SpecialWeaponMount mount){
        return mount.data instanceof HarpoonDataHolder ? (HarpoonDataHolder) mount.data : setupHarpoon(mount);
    }

    public static HarpoonDataHolder setupHarpoon(SpecialWeaponMount mount){
        HarpoonDataHolder harpoon = new HarpoonDataHolder();
        mount.data = harpoon;
        return harpoon;
    }


}
