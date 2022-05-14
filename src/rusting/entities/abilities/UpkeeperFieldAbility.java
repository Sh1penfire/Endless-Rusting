package rusting.entities.abilities;

import arc.Core;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ai.BlockIndexer;
import mindustry.entities.Units;
import mindustry.entities.abilities.RepairFieldAbility;
import mindustry.gen.Unit;

public class UpkeeperFieldAbility extends RepairFieldAbility {
    public float buildingHealMultiplier;
    private final BlockIndexer indexer = Vars.indexer;

    public UpkeeperFieldAbility(float amount, float reload, float range, float buildingHealMultiplier){
        super(amount, reload, range);
        this.buildingHealMultiplier = buildingHealMultiplier;
    }

    @Override
    public void update(Unit unit){

        timer += Time.delta;

        if(timer >= reload){
            wasHealed = false;

            if(indexer != null) indexer.eachBlock(unit, range, other -> other.damaged(), other -> {
                if(other.damaged()){
                    healEffect.at(other);
                    wasHealed = true;
                }
                other.heal(amount * buildingHealMultiplier);
            });

            Units.nearby(unit.team, unit.x, unit.y, range, other -> {
                if(other.damaged()){
                    healEffect.at(other);
                    wasHealed = true;
                }
                other.heal(amount);
            });

            if(wasHealed){
                activeEffect.at(unit, range);
            }
            timer = 0;
        }
    }

    @Override
    public String localized(){
        return Core.bundle.get("ERability.upkeeperfield");
    }
}