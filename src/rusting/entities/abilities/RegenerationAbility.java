package rusting.entities.abilities;

import arc.Core;
import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class RegenerationAbility extends Ability {

    public float health;

    public RegenerationAbility(float health){
        this.health = health;
    }

    @Override
    public void update(Unit unit) {
        unit.heal(health * Time.delta);
        unit.clampHealth();
    }

    @Override
    public String localized() {
        return Core.bundle.get("ERability.regeneration");
    }
}
