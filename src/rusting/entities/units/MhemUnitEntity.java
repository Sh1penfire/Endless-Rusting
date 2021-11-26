package rusting.entities.units;

import mindustry.gen.MechUnit;
import rusting.content.RustingUnits;

public class MhemUnitEntity extends MechUnit {



    @Override
    public int classId(){
        return RustingUnits.classID(CraeUnitEntity.class);
    }
}
