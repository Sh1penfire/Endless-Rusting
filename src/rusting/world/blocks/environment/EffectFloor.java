package rusting.world.blocks.environment;

import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.world.Tile;
import rusting.content.Fxr;

public class EffectFloor extends UpdateFloor{

    public Effect effect = Fxr.healingWaterSmoke;
    public float effectChance = 0.015f;

    public EffectFloor(String name) {
        super(name);
    }

    @Override
    public void update(Tile tile) {
        if(Mathf.chance(effectChance)) effect.at(tile.worldx(), tile.worldy(), 0, tile);
    }
}
