package rusting.entities.units;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.UnitEntity;
import rusting.content.RustingUnits;

public class BaseUnitEntity extends UnitEntity {

    @Override
    public String toString() {
        return "BaseUnitEntity#" + id;
    }

    @Override
    public void read(Reads r) {
        super.read(r);
        read(r, r.b());
    }

    public void read(Reads r, byte revision){

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
    public int classId() {
        return RustingUnits.classID(getClass());
    }

}
