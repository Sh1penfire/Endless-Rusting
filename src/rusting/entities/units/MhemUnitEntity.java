package rusting.entities.units;

import mindustry.gen.MechUnit;
import rusting.content.RustingUnits;
import rusting.entities.units.flying.CraeUnitEntity;

public class MhemUnitEntity extends MechUnit {



    @Override
    public int classId(){
        return RustingUnits.classID(CraeUnitEntity.class);
    }
}
