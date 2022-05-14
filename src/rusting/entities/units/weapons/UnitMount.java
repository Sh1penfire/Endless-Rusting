package rusting.entities.units.weapons;

import arc.math.geom.Vec2;
import arc.util.io.Reads;
import arc.util.io.Writes;
import rusting.interfaces.SpecialWeaponsUnit;

public class UnitMount {

    public UnitMount(){

    }

    //mount name used for getting the class's name from a registery
    protected String name = "default";
    //mount's x and y
    public float x = 0, y = 0;
    //mount's owner
    public SpecialWeaponsUnit owner = null;
    //Mount type that it's asigned to.
    public MountType type = null;
    public float reload = 0;
    public float rotation = 0;
    public float recoil = 0;

    //a percentage, do with it what you want
    public float durability = 1;

    public void update(){
        type.update(owner, this);
    }

    public void draw(){
        type.draw(this);
    }

    public Vec2 getPos(){
        return new Vec2(x, y).rotate(getRotation()).add(owner.self().x, owner.self().y);
    }

    public float getRotation(){
        return type.rotateSpeed == 0 ? owner.self().rotation : rotation;
    }

    //type of mount, used in writing
    public String type(){
        return name;
    }

    //always called
    public void read(Reads r, byte revision){

        reload = r.f();
        rotation = r.f();

    }

    public void write(Writes w){

        w.i(type.position);
        writeFirst(w);
    }

    //stuf that's always written first, like reload and such
    protected void writeFirst(Writes w){

        w.f(reload);
        w.f(rotation);

    }
}
