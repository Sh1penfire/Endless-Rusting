package rusting.game;

import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.game.SpawnGroup;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class BaseSpawnGroup extends SpawnGroup {

    public int x, y;
    public boolean offsetPosition = false;
    public float spread = 3;
    public float effectChance = 0.15f;

    public Effect passiveEffect = Fx.none;
    public float passiveEffectChance;
    public float passiveEffectOffset;
    public Object effectData = null;

    public Effect spawnEffect = Fx.spawn, delayedSpawnEffect = Fx.explosion;
    public float spawnEffectDelay = 30;
    public float effectDuration = 9999999f;
    public float submerged = 0;

    public BaseSpawnGroup(UnitType type){
        super(type);
        passiveEffectOffset = type.hitSize;
    }


    @Override
    public Unit createUnit(Team team, int wave) {
        Unit unit = type.create(team);

        if(effect != null){
            unit.apply(effect, effectDuration);
        }

        if(items != null){
            unit.addItem(items.item, items.amount);
        }

        unit.shield = getShield(wave);

        unit.set(x * tilesize, y * tilesize);
        unit.drownTime = submerged;
        spawnEffect(unit);
        unit.add();
        return unit;
    }

    public int untilSpawn(int wave){
        return (wave > end || wave < begin) ? -1 : spacing - (wave - begin) % spacing;
    }

    protected void spawnEffect(Unit unit){
        unit.rotation = unit.angleTo(world.width()/2f * tilesize, world.height()/2f * tilesize);
        unit.apply(StatusEffects.unmoving, 30f);
        unit.add();

        Call.spawnEffect(unit.x, unit.y, unit.rotation, type);

        spawnEffect.at(unit.x, unit.y, unit.rotation, unit);

        Time.run(spawnEffectDelay, () -> delayedSpawnEffect.at(x * tilesize, y * tilesize, 0, effectData));
    }
}
