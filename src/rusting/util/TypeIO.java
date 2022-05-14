package rusting.util;

import arc.util.Log;
import arc.util.io.Reads;
import rusting.entities.units.SpecialWeaponsUnitType;
import rusting.entities.units.weapons.UnitMount;
import rusting.interfaces.SpecialWeaponsUnit;

public class TypeIO {
    public static void readMounts(SpecialWeaponsUnit unit, Reads r, byte revision){
        unit.sMounts().clear();
        int weapons = r.i();
        for (int i = 0; i < weapons; i++) {
            UnitMount mount = ((SpecialWeaponsUnitType) (unit.self().type)).specialMounts.get(r.i()).createMount();

            unit.setupMount(mount);

            Log.info("mount is reading");

            unit.sMounts().get(i).read(r, revision);

            Log.info("mount finished reading");
        }
    }
}
