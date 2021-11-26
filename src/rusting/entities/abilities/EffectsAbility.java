package rusting.entities.abilities;

import arc.Core;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class EffectsAbility extends Ability {

    Effect trailEffect;
    float chance;
    boolean dynamicRotation, drawTrail;
    float angle;

    public EffectsAbility(Effect trailfx, float chance, float angle, boolean dynamicRotation){
        this.trailEffect = trailfx;
        this.chance = chance;
        this.dynamicRotation = dynamicRotation;
        this.angle = angle;
    }

    @Override
    public void update(Unit unit) {
        if (Mathf.chance(chance)) trailEffect.at(unit.x, unit.y, dynamicRotation ? unit.rotation : 0 + angle, unit);
    }

    @Override
    public String localized(){
        return Core.bundle.get("ERability.effects");
    }

}
