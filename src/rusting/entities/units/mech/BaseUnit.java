package rusting.entities.units.mech;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.MechUnit;
import rusting.content.RustingUnits;

public class BaseUnit extends MechUnit {

    @Override
    public String toString() {
        return "BaseUnit#" + id;
    }

    @Override
    public void read(Reads r) {
        super.read(r);
        read(r, r.b());
    }

    public void read(Reads read, byte revision){

    }

    @Override
    public void write(Writes w) {
        super.write(w);
        w.b(revision());
    }

    public byte revision(){
        return 0;
    }


    @Override
    public int classId(){
        return RustingUnits.classID(getClass());
    }
}
