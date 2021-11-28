package rusting.entities.units;

import arc.struct.Seq;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import rusting.entities.units.weapons.SpecialWeapon;
import rusting.interfaces.SpecialWeaponsUnit;

public class SpecialWeaponsUnitType extends UnitType {

    public SpecialWeaponsUnitType(String name) {
        super(name);
    }

    public Seq<SpecialWeapon> specialWeapons = Seq.with();

    @Override
    public void load() {
        super.load();
        specialWeapons.each(SpecialWeapon::load);
    }

    @Override
    public Unit create(Team team) {
        Unit unit = super.create(team);
        if(unit instanceof SpecialWeaponsUnit) ((SpecialWeaponsUnit) unit).initializeSpecialWeapons(this);
        return unit;
    }
}
