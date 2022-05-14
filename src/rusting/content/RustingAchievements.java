package rusting.content;

import arc.Events;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import rusting.Varsr;
import rusting.ctype.SectorBasedAchievement;
import rusting.ctype.UnlockableAchievement;
import rusting.game.RustingEvents.AchievementUnlockEvent;

import static mindustry.Vars.state;

public class RustingAchievements {

    public static UnlockableAchievement
    //planet 1
    shardlingSteps, theBoatmansCursedBoatman, powerfulLight, pulseTeleporterConstructed, planet1Clear,
    //msc
    youMonster, giganticQuestionMark, GTFO;

    
    public static void load() {

        shardlingSteps = new SectorBasedAchievement( "shardling-steps", 36, RustingPlanets.err) {{

        }};

        theBoatmansCursedBoatman = new UnlockableAchievement("the-boatmans-cursed-boatman") {{

            runUnlock = (object) -> {
                if((Vars.state.isCampaign() && state.rules.sector.id == 268) &&
                        Vars.player.unit().hasEffect(RustingStatusEffects.macrosis) &&
                        Vars.player.unit().hasEffect(RustingStatusEffects.macotagus) &&
                        Vars.player.unit().hasEffect(RustingStatusEffects.balancedPulsation) &&
                        Vars.player.unit().hasEffect(StatusEffects.wet) &&
                        Vars.player.unit().hasEffect(StatusEffects.freezing) &&
                        Vars.player.unit().hasEffect(StatusEffects.tarred)
                ){
                    RustingAchievements.theBoatmansCursedBoatman.unlock();
                    Events.fire(new AchievementUnlockEvent(RustingAchievements.theBoatmansCursedBoatman));
                }
            };
        }};

        powerfulLight = new UnlockableAchievement("powerful-light"){{

        }};

        pulseTeleporterConstructed = new UnlockableAchievement("pulse-teleporter-constructed") {{

        }};

        planet1Clear = new UnlockableAchievement("planet-1-clear") {{

        }};

        youMonster = new UnlockableAchievement("you-monster"){{

        }};

        GTFO = new UnlockableAchievement("GTFO"){{
            triggerClass = EventType.WorldLoadEvent.class;
            runUnlock = e -> {
                if(Varsr.debug) {
                    RustingAchievements.GTFO.unlock();
                    Events.fire(new AchievementUnlockEvent(RustingAchievements.GTFO));
                }
            };
        }};
    }
}
