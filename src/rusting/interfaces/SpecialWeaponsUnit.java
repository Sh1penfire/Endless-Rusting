package rusting.interfaces;

import arc.struct.Seq;
import mindustry.gen.Unit;
import rusting.entities.units.SpecialWeaponsUnitType;
import rusting.entities.units.weapons.SpecialWeaponMount;

public interface SpecialWeaponsUnit {
    default Unit self(){
        return null;
    }

    default Seq<SpecialWeaponMount> sMounts(){
        return new Seq<>();
    }

    default void initializeSpecialWeapons(SpecialWeaponsUnitType type){

    }
}
