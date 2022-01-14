package rusting.entities.units;

import arc.struct.Seq;
import mindustry.game.Team;
import mindustry.gen.Unit;
import rusting.entities.units.weapons.MountType;
import rusting.interfaces.SpecialWeaponsUnit;

public class SpecialWeaponsUnitType extends BaseUnitType {

    public SpecialWeaponsUnitType(String name) {
        super(name);
    }

    public Seq<MountType> specialMounts = Seq.with();

    @Override
    public void load() {
        super.load();
        specialMounts.each(MountType::load);
    }

    @Override
    public void init() {
        super.init();
        int index = 0;
        MountType w;
        //setup unit and position
        for (int i = 0; i < specialMounts.size; i++) {
            w = specialMounts.get(i);
            w.unit = this;
            w.position = index;
        }
    }

    @Override
    public Unit create(Team team) {
        Unit unit = super.create(team);
        if(unit instanceof SpecialWeaponsUnit) ((SpecialWeaponsUnit) unit).initializeSpecialWeapons(this);
        return unit;
    }

    @Override
    public boolean hasWeapons() {
        return super.hasWeapons() || specialMounts.size > 0;
    }
}
