package rusting.game;

import mindustry.type.UnitType;

/**
 * A modified version of a normal spawn group, spawns at a set position instead of a spawner's x and y along with a spawn effect. (The effect doesn't save)
 */
public class EnvironmentSpawnGroup extends BaseSpawnGroup {

    public EnvironmentSpawnGroup(UnitType type){
        super(type);
        effectData = type;
    }

}
