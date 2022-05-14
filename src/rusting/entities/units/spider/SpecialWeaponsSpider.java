package rusting.entities.units.spider;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Unit;
import rusting.entities.units.SpecialWeaponsUnitType;
import rusting.entities.units.weapons.UnitMount;
import rusting.interfaces.SpecialWeaponsUnit;
import rusting.util.TypeIO;

public class SpecialWeaponsSpider extends BaseSpiderEntity implements SpecialWeaponsUnit {

    @Override
    public String toString() {
        return "SpecialWeaponsSpider#" + id;
    }

    public Seq<UnitMount> sMounts = Seq.with();

    @Override
    public Unit self() {
        return this;
    }

    @Override
    public Seq<UnitMount> sMounts() {
        return sMounts;
    }

    @Override
    public void update() {
        super.update();
        sMounts.each(w -> w.update());
    }

    @Override
    public void draw() {
        super.draw();
        sMounts.each(w -> w.draw());
    }

    public UnitMount setupMount(UnitMount mount){
        mount.owner = this;
        sMounts.add(mount);
        return mount;
    }

    @Override
    public void initializeSpecialWeapons(SpecialWeaponsUnitType type) {
        type.specialMounts.each(w -> {
            UnitMount mount = w.mountType.get();
            w.init(mount);
            setupMount(mount);
        });
    }

    @Override
    public void read(Reads r, byte revision) {
        super.read(r, revision);
        TypeIO.readMounts(this, r, revision);
    }

    @Override
    public void write(Writes w) {
        super.write(w);
        w.i(sMounts.size);
        sMounts.each(mount -> {
            mount.write(w);
        });
    }
}
