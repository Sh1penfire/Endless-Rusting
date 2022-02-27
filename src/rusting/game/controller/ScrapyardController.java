package rusting.game.controller;

import arc.Core;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Fx;
import rusting.content.*;
import rusting.game.EnvironmentSpawnGroup;
import rusting.game.SectorController;
import rusting.game.modules.waves.DefaultWaveModule;

import static mindustry.Vars.tilesize;

public class ScrapyardController extends SectorController {

    public DefaultWaveModule waves = new DefaultWaveModule();

    public ScrapyardController(){
        waves.groups.addAll(
            new EnvironmentSpawnGroup(RustingUnits.marrow){{
                spacing = 3;
                end = 8;
                effect = RustingStatusEffects.potassiumDeficiency;

                x = 123;
                y = 207;
                submerged = 0.95f;
                spawnEffect = Fxr.regionDrop;
                effectChance = 0.0025f;
                delayedSpawnEffect = Fxr.spawnerBulatExplosion;
                effectData = Core.atlas.find(type.name + "-full", type.region);
            }}
        );
    }

    @Override
    public void update() {
        waves.groups.each(s -> {
            if(s.untilSpawn(Vars.state.wave) != -1){
                float chance = s.effectChance * (1 - s.untilSpawn(Vars.state.wave)/s.spacing);
                for (int i = 0; i < chance; i++) {
                    if(chance > 1 || Mathf.chance(s.type.hitSize * (chance - i))) Fx.ripple.at(s.x * tilesize + Mathf.range(s.type.hitSize), s.y * tilesize + Mathf.range(s.type.hitSize), s.type.hitSize/8, s.effectData);
                }
            }
        });
    }

    @Override
    public void draw() {

    }

    @Override
    public void wave() {
        waves.wave();
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }
}
