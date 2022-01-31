package rusting.entities.units.weapons.mounts;

import arc.math.geom.Vec2;
import arc.util.io.Reads;
import arc.util.io.Writes;
import rusting.entities.units.weapons.MountType;
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
    //Mount type that it's asigned to. If unasigned the mount is considdered an atatchment, and writes more of it's information.
    public MountType type = null;
    public float reload = 0;
    public float rotation = 0;
    //how fast the weapon rotates
    public float rotationSpeed = 0;

    //a percentage, do with it what you want
    public float durability = 1;
    public Object data = null;

    public void update(){

    }

    public void draw(){

    }

    public Vec2 getPos(){
        return new Vec2(x, y).rotate(owner.self().rotation - 90).add(owner.self().x, owner.self().y);
    }

    public float getRotation(){
        return rotationSpeed > 0 ? rotation : owner.self().rotation;
    }

    //type of mount, used in writing
    public String type(){
        return name;
    }

    //always called
    public void read(Reads r, byte revision, boolean attached){
        if(attached) readMount(r, revision);

        reload = r.f();
        rotation = r.f();

    }

    //only called if previously it was not atatched to a weapon
    protected void readMount(Reads r, byte revision){
        x = r.f();
        y = r.f();
        rotationSpeed = r.f();
        durability = r.f();
    }

    public void write(Writes w){
        boolean attached = type == null;
        w.bool(attached);

        if(!attached) {
            w.i(type.position);
            writeWeapon(w);
            writeFirst(w);
            return;
        }

        //if the type is null assume that no weapon has been found
        w.str(name);
        writeMount(w);
        writeFirst(w);
    }

    //stuf that's always written first, like reload and such
    protected void writeFirst(Writes w){

        w.f(reload);
        w.f(rotation);

    }

    //called when not asigned to a weapon
    protected void writeMount(Writes w){

        w.f(x);
        w.f(y);
        w.f(rotationSpeed);
        w.f(durability);

    }

    //called when asigned to a weapon
    protected void writeWeapon(Writes w){

    }
}
