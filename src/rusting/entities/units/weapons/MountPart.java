package rusting.entities.units.weapons;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

import static mindustry.type.UnitType.outlineSpace;

public class MountPart {

    public float groundLayer = Layer.groundUnit;
    public boolean top = false;
    //offset from the normal drawing layer of the mount
    public float layerOffset = 0;
    //x and y positions of the part
    public float x = 0, y = 0;
    //recoil of the part
    public float recoil = 0;
    //name of the part, used for finding the regions
    public String name;
    //regions used in drawing
    public TextureRegion region, outlineRegion;

    public MountPart(String name){
        this.name = name;
    }

    public void init(MountType type){
         x = type.x;
         y = type.y;
         recoil = type.recoil;
    }

    public void load(){
        region = Core.atlas.find(name, Core.atlas.find("clear"));
        outlineRegion = Core.atlas.find(name + "-outline", Core.atlas.find("clear"));
    }

    public void draw(UnitMount mount){
        UnitType type = mount.owner.self().type;
        float z = (mount.owner.self().elevation > 0.5f ? (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(mount.owner.self().hitSize / 4000f, 0, 0.01f)) + layerOffset;
        if(!top) Draw.z(z - outlineSpace);
        else Draw.z(z);
        Draw.rect(outlineRegion, Tmp.v1.set(getPos(mount)).x, Tmp.v1.y, mount.getRotation() - 90);
        Draw.z(z);
        Draw.rect(region, Tmp.v1.set(getPos(mount)).x, Tmp.v1.y, mount.getRotation() - 90);
    }

    //purely visual pos
    public Vec2 getPos(UnitMount mount){
        return new Vec2(x, y - mount.recoil * recoil).rotate(mount.getRotation() - 90).add(mount.owner.self().x, mount.owner.self().y);
    }
}
