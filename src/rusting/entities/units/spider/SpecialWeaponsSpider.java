package rusting.entities.units.spider;

import arc.struct.Seq;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import rusting.content.RustingUnits;
import rusting.entities.units.SpecialWeaponsUnitType;
import rusting.entities.units.weapons.SpecialWeaponMount;
import rusting.interfaces.SpecialWeaponsUnit;

public class SpecialWeaponsSpider extends BaseSpiderEntity implements SpecialWeaponsUnit {

    @Override
    public String toString() {
        return "SpecialWeaponsSpider#" + id;
    }

    public Seq<SpecialWeaponMount> sMounts = Seq.with();

    @Override
    public Unit self() {
        return this;
    }

    @Override
    public Seq<SpecialWeaponMount> sMounts() {
        return sMounts;
    }

    @Override
    public void update() {
        super.update();
        sMounts.each(w -> w.weapon.update(w));
    }

    @Override
    public void draw() {
        super.draw();
        sMounts.each(w -> w.weapon.draw(w));
    }

    @Override
    public void initializeSpecialWeapons(SpecialWeaponsUnitType type) {
        type.specialWeapons.each(w -> {
            sMounts.add(new SpecialWeaponMount(w, this));
        });
    }

    @Override
    public void setType(UnitType type) {
        super.setType(type);
        if(type instanceof SpecialWeaponsUnitType) initializeSpecialWeapons((SpecialWeaponsUnitType) type);
    }

    @Override
    public int classId() {
        return RustingUnits.classID(SpecialWeaponsSpider.class);
    }
}
