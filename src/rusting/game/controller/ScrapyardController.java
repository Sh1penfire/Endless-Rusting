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

    public float spawnParticleMulti = 3.15f;
    public DefaultWaveModule waves = new DefaultWaveModule();

    public ScrapyardController(){
        waves.groups.addAll(
            new EnvironmentSpawnGroup(RustingUnits.epiphysis){{
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
                float untilWaves = s.untilSpawn(Vars.state.wave);
                float chance = s.effectChance * ((s.spacing - untilWaves)/s.spacing) * s.type.hitSize;
                for (int i = 0; i < chance; i++) {
                    if(chance > 1 || Mathf.chance(untilWaves == s.spacing ? spawnParticleMulti * (chance - i) : chance - i)) Fx.ripple.at(s.x * tilesize + Mathf.range(s.type.hitSize), s.y * tilesize + Mathf.range(s.type.hitSize), s.type.hitSize/8, Vars.world.floor(s.x, s.y).mapColor, null);
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
