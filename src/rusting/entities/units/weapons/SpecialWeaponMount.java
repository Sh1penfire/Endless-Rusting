package rusting.entities.units.weapons;

import arc.math.geom.Vec2;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import rusting.interfaces.SpecialWeaponsUnit;

public class SpecialWeaponMount {

    public SpecialWeaponMount(SpecialWeapon weapon, SpecialWeaponsUnit owner){
        this.weapon = weapon;
        this.owner = owner;
        weapon.init(this);
    }

    public SpecialWeapon weapon = null;
    public SpecialWeaponsUnit owner = null;
    public float reload = 0;
    public float rotation = 0;

    //a percentage, do with it what you want
    public float durability = 1;
    public Object data = null;

    public Vec2 getPos(){
        return Tmp.v1.set(weapon.x, weapon.y).rotate(owner.self().rotation - 90).add(owner.self().x, owner.self().y);
    }

    public float getRotation(){
        return weapon.rotates ? rotation : owner.self().rotation;
    }

    public void read(Reads read, Byte revision){

    }

    public void write(Writes write){

    }
}
