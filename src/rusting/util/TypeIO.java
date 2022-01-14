package rusting.util;

import arc.util.io.Reads;
import rusting.entities.units.SpecialWeaponsUnitType;
import rusting.entities.units.weapons.MountType;
import rusting.entities.units.weapons.mounts.UnitMount;
import rusting.interfaces.SpecialWeaponsUnit;

public class TypeIO {
    public static void readMounts(SpecialWeaponsUnit unit, Reads r, byte revision){
        unit.sMounts().clear();
        int weapons = r.i();
        for (int i = 0; i < weapons; i++) {
            boolean atatched = r.bool();
            UnitMount mount = atatched ? MountType.registry.get(r.str()).get() : ((SpecialWeaponsUnitType) (unit.self().type)).specialMounts.get(r.i()).mountType.get();
            unit.setupMount(mount);

            unit.sMounts().get(i).read(r, revision, atatched);
        }
    }
}
