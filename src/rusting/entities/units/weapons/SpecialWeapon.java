package rusting.entities.units.weapons;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

import static mindustry.type.UnitType.outlineSpace;

public class SpecialWeapon {

    public SpecialWeapon(String name){
        this.name = name;
    }

    //the name of the weapon
    public String name;

    public TextureRegion region, outlineRegion, fullRegion;
    //the x and y of the weapon, which shifts with the unit
    public float x = 0, y = 0;

    //whether weapon rotates or not
    public boolean rotates = false;
    //whether the outline on this mount is drawn above other mounts of the same type
    public boolean top = false;

    public float rotateSpeed = 2;

    public void load(){
        region = Core.atlas.find(name);
        outlineRegion = Core.atlas.find(name + "-outline", Core.atlas.find("clear"));
        fullRegion = Core.atlas.find(name + "-full", region);
    }

    //called upon a new mount being initialized
    public void init(SpecialWeaponMount mount){

    }

    //left empty, this is the base class
    public void update(SpecialWeaponMount mount){

    }

    //override this to draw the weapon
    public void draw(SpecialWeaponMount mount){
        UnitType type = mount.owner.self().type;
        float z = mount.owner.self().elevation > 0.5f ? (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : type.groundLayer + Mathf.clamp(mount.owner.self().hitSize / 4000f, 0, 0.01f);
        Draw.z(z);
        Draw.rect(region, Tmp.v1.set(mount.getPos()).x, Tmp.v1.y, mount.getRotation() - 90);
        if(!top) Draw.z(z - outlineSpace);
        Draw.rect(outlineRegion, Tmp.v1.set(mount.getPos()).x, Tmp.v1.y, mount.getRotation() - 90);
    }
}