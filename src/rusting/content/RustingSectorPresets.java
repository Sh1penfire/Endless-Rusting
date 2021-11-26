package rusting.content;

import arc.func.Boolf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.core.GameState;
import mindustry.ctype.ContentList;
import mindustry.gen.Groups;
import rusting.game.ERSectorPreset;
import rusting.util.MusicControl.MusicSecController.MusicSecSegment;

public class RustingSectorPresets implements ContentList {
    public static ERSectorPreset
    incipiensGrounds, plantaePresevereDomae, volenChannels, paileanCorridors, saltyShoals, overgrownMines, abystrikenCrevasse, crystallineCrags, pulsatingGroves, sulphuricSea, hangout, preservatory;
    ;

    @Override
    public void load() {
        incipiensGrounds = new ERSectorPreset("incipiens-grounds", RustingPlanets.err, 36){{
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 23;
            difficulty = 1f;
        }};

        plantaePresevereDomae = new ERSectorPreset("plantae-presevere-domae", RustingPlanets.err, 196){{
            difficulty = 1f;
        }};

        volenChannels = new ERSectorPreset("volen-channels", RustingPlanets.err, 154){{
            difficulty = 4;
        }};

        paileanCorridors = new ERSectorPreset("pailean-corridors", RustingPlanets.err, 187){{
            captureWave = 45;
            difficulty = 3;
        }};

        saltyShoals = new ERSectorPreset("salty-shoals", RustingPlanets.err, 1){{
            captureWave = 40;
            difficulty = 6;

            musicSecController.musicMap.addAll(
                ObjectMap.of(
                    (Boolf<GameState>) gameState -> gameState.rules.spawns.find(s -> s.effect == StatusEffects.boss && (gameState.wave - s.begin) % s.spacing ==  s.spacing) != null || Groups.unit.find(u -> u.team == Vars.state.rules.defaultTeam && u.hasEffect(StatusEffects.boss)) != null,
                    Seq.with(new MusicSecSegment(1, true){{
                        musicChance = 1;
                        duration = 15;
                    }})
                )
            );
        }};

        overgrownMines = new ERSectorPreset("overgrown-mines", RustingPlanets.err, 97){{
            captureWave = 65;
            difficulty = 8;
        }};

        abystrikenCrevasse = new ERSectorPreset("abystriken-crevasse", RustingPlanets.err, 25){{
            captureWave = 38;
            difficulty = 4;
        }};

        crystallineCrags = new ERSectorPreset("crystalline-crags", RustingPlanets.err, 268){{
            captureWave = 40;
            difficulty = 4;

            musicSecController.musicMap.addAll(
                    ObjectMap.of(
                        (Boolf<GameState>) gameState -> gameState.wave > 3,
                        Seq.with(new MusicSecSegment(0, false))
                    ),
                    ObjectMap.of(
                        (Boolf<GameState>) gameState -> gameState.rules.spawns.find(s -> s.effect == StatusEffects.boss && (gameState.wave - s.begin) % s.spacing ==  s.spacing) != null || Groups.unit.find(u -> u.team == Vars.state.rules.defaultTeam && u.hasEffect(StatusEffects.boss)) != null,
                        Seq.with(new MusicSecSegment(1, true){{
                            musicChance = 1;
                        }})
                    )
            );
        }};

        pulsatingGroves = new ERSectorPreset("pulsating-groves", RustingPlanets.err, 56){{
            difficulty = 5;
            useAI = false;
        }};

        sulphuricSea = new ERSectorPreset("sulphur-seas", RustingPlanets.err, 0){{
            difficulty = 8;
            useAI = false;
            rules = (rules) -> {
                rules.tags.put("events.er.stingrayfail", "true");
            };
        }};

        hangout = new ERSectorPreset("pure-past-void-future", RustingPlanets.err, 35){{
            difficulty = 0;
            captureWave = 0;

        }};

        preservatory = new ERSectorPreset("lush-preservatory", RustingPlanets.err, 110){{
            difficulty = 0;
            captureWave = 0;
        }};

    }
}
