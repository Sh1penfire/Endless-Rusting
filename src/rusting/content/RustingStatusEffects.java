package rusting.content;

import arc.Events;
import arc.func.Cons;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.core.Version;
import mindustry.ctype.ContentList;
import mindustry.entities.Units;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import rusting.type.statusEffect.*;

//first time I'm doing this
@SuppressWarnings("unchecked")

public class RustingStatusEffects implements ContentList {
    public static StatusEffect
            weather, hpooned, fuesin, amberstriken, umbrafliction, macrosis, macotagus, balancedPulsation, hailsalilty, causticBurning, deceptione, blackout, potassiumDeficiency, shieldShatter, corruptShield, fragmentaein, guardiansBlight;
    public static Cons
            corruptShieldCons;

    private float voidRepulseShieldRange = 64;
    private FloatSeq floatSeq;
    private float index;

    @Override
    public void load() {

        //sued by weather and to see if unit was alive during a weather effect
        weather = new StatusEffect("weather"){

        };

        //used by harpoons to stop harpoons from targeting the same target as another
        hpooned = new StatusEffect("hpooned"){

        };

        //reacts with a lot of status effects to create others
        //the statement above me is a lie
        fuesin = new ConsStatusEffect("fuesin"){{
            init(() -> {
                if(Version.number > 6) return;
                transitioning(macrosis, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.apply(macotagus, time * 3);
                    unit.unapply(this);
                }));
                transitioning(StatusEffects.freezing, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.apply(StatusEffects.wet, time * 3);
                    unit.unapply(this);
                }));
                transitioning(StatusEffects.burning, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.apply(StatusEffects.melting, time * 3);
                    unit.unapply(this);
                }));
            });
        }};

        amberstriken = new CrystalStatusEffect("amberstriken"){{
            speedMultiplier = 0.35F;
            transitionDamage = 35f;
            hitSizeMax = 16;
            effect = Fx.plasticburn;
            init(() -> {
                transitioning(StatusEffects.burning, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.apply(umbrafliction, time * 3);
                    Fx.placeBlock.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
                    unit.unapply(this);
                }));
                transitioning(umbrafliction, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage/3);
                    unit.unapply(amberstriken);
                }));
                transitioning(StatusEffects.blasted, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.unapply(this);
                }));
            });
            disarm = false;
        }};

        umbrafliction = new CrystalStatusEffect("umbrafliction"){{
            transitionDamage = 45;
            speedMultiplier = 0.15F;
            hitSizeMax = 21;
            effect = Fxr.blackened;
            init(() -> {
                transitioning(StatusEffects.blasted, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.unapply(this);
                }));
            });
            disarm = false;
        }};

        hailsalilty = new ERStatusEffect("hailsalilty"){{
            effect = Fxr.salty;
            init(() -> {
                transitioning(StatusEffects.wet, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.apply(StatusEffects.corroded, time * 3);
                    Fx.plasticburn.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
                    unit.unapply(this);
                }));
            });
            disarm = false;
        }};

        macrosis = new SpreadingStatusEffect("macrosis"){{
            damage = 0.05f;
            speedMultiplier = 0.75f;
            buildSpeedMultiplier = 0.75f;
            dragMultiplier = 0.85f;
            damageMultiplier = 0.88f;
            spreadInterval = 85;
            effect = Fxr.craeWeaversResidue;
            updateCons = (unit, time) -> {
                unit.impulse(Tmp.v1.trns(unit.rotation, unit.type.accel * 8).clamp(0, Tmp.v3.trns(unit.type.speed/60, unit.rotation).sub(unit.vel()).len()));
            };
        }};

        macotagus = new SpreadingStatusEffect("macotagus"){{
            damage = 0.135f;
            spreadSingle = false;
            speedMultiplier = 0.45f;
            buildSpeedMultiplier = 0.55f;
            dragMultiplier = 0.85f;
            damageMultiplier = 0.82f;
            spreadInterval = 120;
            effect = Fxr.craeWeaverShards;
            effectChance = 0.15f;
            updateCons = (unit, time) -> {
                float rotation = unit.rotation;
                Unit nearestAlly = null;
                if(time % 5 < 1){
                    nearestAlly = Units.closest(unit.team, unit.x, unit.y, spreadRadius * 3, u -> true);
                    if(nearestAlly != null) {
                        rotation = Mathf.lerp(unit.angleTo(nearestAlly), unit.rotation, 0.15f  * Time.delta);
                    }
                }
                unit.impulse(Tmp.v1.trns(rotation, nearestAlly != null ? unit.type.accel * 32 * (2 - speedMultiplier) : unit.type.accel * 8 * (2 - speedMultiplier)));
            };
        }};

        balancedPulsation = new SpreadingStatusEffect("balanced-pulsation"){{
            spreadSingle = false;
            spreadEnnemeis = true;
            speedMultiplier = 0.75f;
            buildSpeedMultiplier = 0.35f;
            dragMultiplier = 0.45f;
            damageMultiplier = 0.72f;
            spreadInterval = 55;
            effect = Fxr.craeWeaverShards;
            effectChance = 0.15f;
            updateCons = (unit, time) -> {
                float rotation = unit.rotation - 180;
                unit.impulse(Tmp.v1.trns(rotation, Time.delta * (unit.isPlayer() ? unit.type.accel * 37 * (2 - speedMultiplier) : unit.type.speed * 5 + unit.type.drag * 16)));
            };
        }};

        //does more damage the more damaged the unit is. Strangely heals the unit overtime.
        causticBurning = new ConsStatusEffect("caustic-burning"){{
            speedMultiplier = 0.85f;
            buildSpeedMultiplier = 0.85f;
            dragMultiplier = 0.425f;
            damageMultiplier = 0.92f;
            effect = Fx.plasticburn;
            effectChance = 0.025f;
            transitionDamage = 15;

            updateCons = (unit, time) -> {
                if(unit.damaged() && unit.healthf() > 0.15f && unit.healthf() < 0.85f) unit.damagePierce(Math.min((unit.type.health - unit.health)/60, Math.min(10/60f, unit.health / 2500)) * Time.delta);
                else unit.heal(0.125f);
            };

            init(() -> {
                transitioning(StatusEffects.burning, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage/15);
                    unit.apply(StatusEffects.corroded, time * 3);
                    unit.unapply(this);
                    Fx.burning.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
                }));
                transitioning(StatusEffects.corroded, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage/60);
                    Fx.plasticburn.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
                }));
            });
        }};

        deceptione = new StatusEffect("deceptione"){{
            damage = 11;
            speedMultiplier = 1.15f;
            damageMultiplier = 1.05f;
            dragMultiplier = 2;
        }};

        blackout = new StatusEffect("blackout"){{

        }};

        potassiumDeficiency = new ConsStatusEffect("potassium-deficiency"){{
            speedMultiplier = 1.35f;
            dragMultiplier = 1.65f;
            buildSpeedMultiplier = 0.35f;
            damageMultiplier = 0.85f;
            
            effect = Fx.plasticburn;
            effectChance = 0.025f;
        }};

        shieldShatter = new ConsStatusEffect("shield-shatter"){{
            transitionDamage = 15;
            speedMultiplier = 0.85f;
            effect = Fx.generatespark;

            init(() -> {

                opposite(StatusEffects.wet);

                transitioning(StatusEffects.burning, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.apply(StatusEffects.corroded, time * 3);
                }));
            });

            updateCons = (unit, time) -> {
                if(unit.shield() > 0) unit.damage(Mathf.clamp(unit.shield(), unit.shield(), 125/60) * Time.delta);
            };
        }};

        corruptShield = new ConsStatusEffect("corrupt-shield"){{
            speedMultiplier = 1.15f;
            effect = Fxr.blackened;
            init(() -> {
                opposite(fragmentaein, balancedPulsation, macrosis, macotagus);
            });
        }};

        fragmentaein = new ConsStatusEffect("fragmentaein"){{
            transitionDamage = 15;
            effect = Fx.bubble;
            init(() -> {
                if(Version.number > 6) return;
                transitioning(amberstriken, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.damage(amberstriken.transitionDamage);
                    Fx.plasticburn.at(unit.x, unit.y);
                    unit.unapply(amberstriken);
                }));
                transitioning(umbrafliction, ((unit, time, newTime) -> {
                    unit.damagePierce(transitionDamage);
                    unit.damage(umbrafliction.transitionDamage);
                    Fxr.blackened.at(unit.x, unit.y);
                    unit.unapply(amberstriken);
                }));
            });
        }};

        //a guardian effect that deals percentile damage to the unit, equivalent to type.health/10000/healthMultiplier * delta;  Will react with a lot of the basegame status effects, and even some of ER's effects.
        guardiansBlight = new ConsStatusEffect("guardians-blight"){{
            updateCons = (unit, time) -> {
                unit.damagePierce(unit.type.health/10000/unit.healthMultiplier() * Time.delta);
            };
        }};

        //you know I had to
        StatusEffects.corroded.effect = Fxr.corrodedEffect;

        corruptShieldCons = new Cons() {
            @Override
            public void get(Object o) {

                //iterate through all bullets
                Groups.bullet.each(b -> {

                    if(Vars.state.isPaused()) return;
                    if(!b.type.reflectable) return;
                    float range = 64;

                    Seq<Unit> intUnits = Groups.unit.intersect(b.x - range, b.y - range, range * 2, range * 2);
                    FloatSeq forces = new FloatSeq();
                    float[] greatestDistance = {0};

                    intUnits.each(u -> {

                        if(!u.hasEffect(corruptShield) || u.team == b.team) return;

                        float distance = u.dst(b);
                        if(distance > greatestDistance[0]) greatestDistance[0] = distance;
                    });

                    intUnits.each(u -> {

                        if(!u.hasEffect(corruptShield) || u.team == b.team) return;

                        float distance = u.dst(b);
                        float force = Mathf.floor(range - u.dst(b)) * distance/greatestDistance[0];
                        forces.add(force);
                    });

                    int[] index = {0};

                    intUnits.each(u -> {

                        if(!u.hasEffect(corruptShield) || u.team == b.team) return;

                        b.vel.setAngle(Angles.moveToward(b.rotation(), u.angleTo(b), forces.get(index[0]) * Time.delta/30));
                        index[0]++;
                    });
                });

            }
        };

        Events.on(Trigger.update.getClass(), corruptShieldCons);
    }
}
