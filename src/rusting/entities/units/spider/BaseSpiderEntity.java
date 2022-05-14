package rusting.entities.units.spider;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.LegsUnit;
import rusting.content.RustingUnits;

public class BaseSpiderEntity extends LegsUnit {

    @Override
    public String toString() {
        return "BaseSpiderEntity#" + id;
    }

    @Override
    public void read(Reads r) {
        super.read(r);
        read(r, r.b());
    }

    @Override
    public boolean isShooting() {
        return super.isShooting();
    }

    public void read(Reads r, byte revision){

    }

    @Override
    public void write(Writes w) {
        super.write(w);
        w.b(revision());
    }

    public byte revision() {
        return 0;
    }


    @Override
    public int classId() {
        return RustingUnits.classID(getClass());
    }
}
