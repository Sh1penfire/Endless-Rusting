package rusting;

import arc.Core;
import arc.Events;
import arc.KeyBinds.Axis;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.game.EventType;
import mindustry.game.EventType.FileTreeInitEvent;
import mindustry.game.EventType.Trigger;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.input.Binding;
import mindustry.mod.Mod;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import rusting.content.*;
import rusting.graphics.*;
import rusting.math.Mathr;
import rusting.type.statusEffect.CrystalStatusEffect;

import static arc.Core.scene;

public class EndlessRusting extends Mod{

    public static String modname = "endless-rusting";

    public static RustedSettingAdder settingAdder = new RustedSettingAdder();
    private static Seq<UnitType> immunityUnits;
    private static Seq<UnitType> empImmune;
    private static Seq<StatusEffect> whitelistedStuns = Seq.with(StatusEffects.unmoving);

    public EndlessRusting(){

        Varsr.music.init();

        Events.on(FileTreeInitEvent.class, e -> {
            ModSounds.load();
            Core.app.post(RustedShaders::load);
        });
        registerKeybinds();

        Core.settings.defaults("er.drawtrails", true);
        Core.settings.defaults("er.advancedeffects", true);

        Events.on(EventType.ClientLoadEvent.class,
            e -> {
                Graphicsr.loadReplacementCursors();
                setup();
            }
        );

        Events.on(EventType.ContentInitEvent.class, e -> {
            Varsr.content.init();
            Log.info(Mathr.status(-5));
            Log.info(Mathr.status(69));
        });

        Events.on(EventType.UnitCreateEvent.class,
            e -> {
                if(e.unit.type == RustingUnits.stingray && ((Vars.state.rules.tags.getBool("events.er.stingrayfail") && e.spawner.team == Vars.state.rules.waveTeam) || !Core.settings.getBool("settings.er.stingrayloyal") && e.spawner.team == Vars.state.rules.defaultTeam && !Varsr.debug)){
                    if(!Vars.headless) {
                        callNukestorm(7, 6, e.spawner.team == Vars.state.rules.defaultTeam ? true : false, e.spawner.rotation * 90, e.spawner.x, e.spawner.y, 25);
                    }
                    if(e.spawner.team != Vars.state.rules.defaultTeam) {
                        CoreBuild coreBlock = Vars.state.teams.closestCore(0, 1400, Vars.state.rules.defaultTeam);
                        if (coreBlock != null) {
                            for (int i = 0; i < 4; i++) {
                                Tmp.v1.trns(i * 90, 55).add(coreBlock.x, coreBlock.y);
                                RustingUnits.observantly.spawn(Vars.state.rules.defaultTeam, 0, Vars.world.height());
                                if (!Vars.headless) Call.spawnEffect(Tmp.v1.x, Tmp.v1.y, 25, RustingUnits.observantly);
                            }
                            for (int i = 0; i < 8; i++) {
                                Tmp.v1.trns(i * 45, 55).add(0, Vars.world.height());
                                RustingUnits.kindling.spawn(Vars.state.rules.defaultTeam, Tmp.v1.x, Tmp.v1.y);
                                if (!Vars.headless) Call.spawnEffect(Tmp.v1.x, Tmp.v1.y, 25, RustingUnits.kindling);
                            }
                        }
                    }
                }
            }
        );
    }

    private void callNukestorm(int groups, int missiles, boolean useSpawnerPos, float rotation, float x, float y, float anglefromSky){
        for (int j = 0; j < groups; j++) {
            for (int i = 0; i < missiles; i++) {
                Time.run(i * 65 + Mathf.random(85), () -> {
                    Tmp.v2.set(0, 0);
                    if(!useSpawnerPos) Tmp.v2.trns(rotation, 350).add( 64 - Mathf.random(128), 128 - Mathf.random(128));
                    Tmp.v2.add(x, y);
                    Tmp.v1.trns(anglefromSky, RustingBullets.craeNukestorm.range() * 3).add(5 - Mathf.random(10), 5 - Mathf.random(10));
                    Call.createBullet(RustingBullets.craeNukestorm, Team.blue, Tmp.v2.x + Tmp.v1.x, Tmp.v2.y + Tmp.v1.y, Tmp.v1.add(Tmp.v2).angleTo(Tmp.v2.x, Tmp.v2.y), RustingBullets.craeNukestorm.damage, 1, 3);
                });
            }
            Time.run(j * 145 + Mathf.random(195), () -> {
                Tmp.v2.set(0, 0);
                if(!useSpawnerPos) Tmp.v2.trns(rotation, 350).add( 64 - Mathf.random(128), 128 - Mathf.random(128));
                Tmp.v2.add(x, y);
                Tmp.v1.trns(anglefromSky, RustingBullets.craeBalistorm.range() * 3).add(5 - Mathf.random(10), 5 - Mathf.random(10));
                Call.createBullet(RustingBullets.craeBalistorm, Team.blue, Tmp.v2.x + Tmp.v1.x, Tmp.v2.y + Tmp.v1.y, Tmp.v1.add(Tmp.v2).angleTo(Tmp.v2.x, Tmp.v2.y), RustingBullets.craeBalistorm.damage, 1, 3);
            });
        }
    }

    @Override
    public void init(){
        Varsr.init();
    }

    //called after all content is loaded. can be called again, for debugging.
    public void setup(){
        Varsr.setup();
        settingAdder.init();
        Varsr.content.load();
        immunityUnits = Seq.with(RustingUnits.stingray);
        empImmune = Seq.with(RustingUnits.marrow, RustingUnits.metaphys, RustingUnits.ribigen, RustingUnits.spinascene, RustingUnits.trumpedoot);
        Seq<UnitType> walls = Seq.with(RustingUnits.pulseBarrenBezerker);
        final Seq<StatusEffect> statusEffectSeq = Seq.with();

        walls.each(w -> {
            statusEffectSeq.clear();
            statusEffectSeq.addAll(w.immunities);
            w.immunities.clear();
            Vars.content.statusEffects().each(s -> {
                if(!statusEffectSeq.contains(s)) w.immunities.add(s);
            });
        });

        Vars.content.statusEffects().each(s -> {
            //chek for NaN damage
            if(!s.name.contains("endless-rusting") && !s.name.contains("pixelcraft") && (s.disarm == true || s.damage == s.damage && s.damage >= 1 || s.speedMultiplier <= 0.85f || s.healthMultiplier < 0.85f || s.damageMultiplier < 0.85f)) immunityUnits.each(unit -> unit.immunities.add(s));
            if(hasPower(s.name) || hasPower(s.localizedName) || s.description != null && hasPower(s.description)) empImmune.each(u -> u.immunities.add(s));
        });
        RustingUnits.stingray.immunities.remove(StatusEffects.melting);
        RustingUnits.stingray.immunities.remove(StatusEffects.tarred);

        Vars.content.units().each(u -> {
            CrystalStatusEffect.crystalStatusEffectSeq.each(s -> {
                if(u.hitSize > s.hitSizeMax) u.immunities.add(s);
            });
        });
    }

    private static boolean hasPower(String string){
        if(string == null) return false;
        String lowerString = string;
        lowerString.toLowerCase();
        return lowerString.contains("emp") || string.contains("electri") || string.contains("electrif") || string.contains("shocked");
    }

    @Override
    public void loadContent(){
        Drawr.setMethods();
        Varsr.content.createContent();
        Color.cyan.set(Palr.pulseChargeEnd);
        Color.sky.set(Palr.pulseChargeStart);
    }

    public void registerKeybinds(){
        Events.on(Trigger.update.getClass(), e -> {
            if(!scene.hasField() && !scene.hasDialog()){
                if(Core.input.keyTap(KeyCode.f2)) Varsr.ui.achievements.show();
                if(Core.input.keyTap(KeyCode.n)) Graphicsr.resetCursors();
                if(Core.input.keyTap(KeyCode.b)) Graphicsr.corsairCursor();
            }
        });
    }
}
