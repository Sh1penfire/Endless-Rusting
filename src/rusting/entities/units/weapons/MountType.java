package rusting.entities.units.weapons;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import rusting.entities.units.SpecialWeaponsUnitType;
import rusting.interfaces.SpecialWeaponsUnit;

public class MountType {

    //all mounts
    public static Seq<MountType> mounts = Seq.with();

    //mount parts
    public Seq<MountPart> parts = Seq.with();
    public boolean useParts = true;

    public MountType(String name){
        this.name = name;
        mounts.add(this);
    }

    //the name of the weapon
    public String name;

    //the default x and y of the weapon, which shifts with the unit
    public float x = 0, y = 0;
    //whether weapon rotates or not
    public boolean rotates = false;

    //whether the outline on this mount is drawn above other mounts of the same type
    public boolean top = false;

    //the default speed at which the mount rotates at
    public float rotateSpeed = 10;

    //recoil
    public float recoil = 0;

    //recoil lost per tick
    public float restitution = 0.12f;

    public TextureRegion region, outlineRegion, fullRegion;

    public MountProvider<UnitMount> mountType = UnitMount::new;
    //the unit type
    public SpecialWeaponsUnitType unit;
    //position of the weapon in the seq, for usage in saving and loading
    public int position;

    public void load(){
        region = Core.atlas.find(name);
        outlineRegion = Core.atlas.find(name + "-outline", Core.atlas.find("clear"));
        fullRegion = Core.atlas.find(name + "-full", region);
        if(parts.size == 0 || !useParts) {
            parts.add(new MountPart(name) {{

            }});
            parts.get(0).init(this);
        }
        parts.each(p -> p.load());
    }

    public UnitMount createMount(){
        return init(mountType.get());
    }

    //called upon a new mount being initialized
    public UnitMount init(UnitMount mount){
        mount.type = this;
        mount.x = x;
        mount.y = y;
        return mount;
    }

    public void update(SpecialWeaponsUnit unit, UnitMount mount){

    }

    //override this to draw the weapon
    public void draw(UnitMount mount){
        parts.each(m -> m.draw(mount));
    }

    public interface MountProvider<T>{
        T get();
    }
}