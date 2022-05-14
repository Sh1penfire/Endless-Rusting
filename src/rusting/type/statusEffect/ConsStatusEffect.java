package rusting.type.statusEffect;

import arc.func.Cons2;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class ConsStatusEffect extends ERStatusEffect {

    public Cons2<Unit, Float> updateCons;

    public ConsStatusEffect(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit, float time) {
        super.update(unit, time);
        if(updateCons != null) updateCons.get(unit, time);
    }


}
