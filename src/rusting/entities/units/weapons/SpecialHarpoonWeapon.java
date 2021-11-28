package rusting.entities.units.weapons;

import arc.func.Cons;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

public class SpecialHarpoonWeapon extends SpecialBulletWeapon{
    public SpecialHarpoonWeapon(String name) {
        super(name);
    }

    public class HarpoonDataHolder{

        public Vec2 harpoonPosition = new Vec2(x, y);
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
}
