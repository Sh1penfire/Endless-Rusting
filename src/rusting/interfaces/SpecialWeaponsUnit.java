package rusting.interfaces;

import arc.struct.Seq;
import mindustry.gen.Unit;
import rusting.entities.units.SpecialWeaponsUnitType;
import rusting.entities.units.weapons.mounts.UnitMount;

public interface SpecialWeaponsUnit {

    default Unit self(){
        return null;
    }

    default Seq<UnitMount> sMounts(){
        return new Seq<>();
    }

    default void initializeSpecialWeapons(SpecialWeaponsUnitType type){
        type.specialMounts.each(m -> {
            UnitMount mount = m.mountType.get();
            mount.owner = this;
            m.init(mount);
            sMounts().add(mount);
        });
    }

    UnitMount setupMount(UnitMount mount);
}
