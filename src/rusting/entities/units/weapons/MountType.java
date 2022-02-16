package rusting.entities.units.weapons;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import rusting.entities.units.SpecialWeaponsUnitType;
import rusting.interfaces.SpecialWeaponsUnit;

import static mindustry.type.UnitType.outlineSpace;

public class MountType {

    //all mounts
    public static Seq<MountType> mounts = Seq.with();

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
        UnitType type = mount.owner.self().type;
        float z = mount.owner.self().elevation > 0.5f ? (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : type.groundLayer + Mathf.clamp(mount.owner.self().hitSize / 4000f, 0, 0.01f);
        Draw.z(z);
        Draw.rect(region, Tmp.v1.set(mount.getPos()).x, Tmp.v1.y, mount.getRotation() - 90);
        if(!top) Draw.z(z - outlineSpace);
        Draw.rect(outlineRegion, Tmp.v1.set(mount.getPos()).x, Tmp.v1.y, mount.getRotation() - 90);
    }

    public interface MountProvider<T>{
        T get();
    }
}