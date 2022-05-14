package rusting;

import arc.Core;
import arc.Events;
import arc.assets.Loadable;
import arc.math.Mathf;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.core.Version;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.net.Net;
import mindustry.net.Packet;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.meta.BuildVisibility;
import rusting.ai.AISwitches;
import rusting.content.*;
import rusting.core.RustedContentLoader;
import rusting.core.Rusting;
import rusting.ctype.UnlockableAchievement;
import rusting.entities.abilities.SpeedupAbility;
import rusting.game.*;
import rusting.graphics.VelocityTrail;
import rusting.net.ControlPacket;
import rusting.ui.RustingUI;
import rusting.util.MusicControl;
import rusting.world.Worldr;
import rusting.world.blocks.PlayerCore;
import rusting.world.blocks.defense.turret.BerthaTurret;
import rusting.world.blocks.pulse.distribution.PulseCanal.PulseCanalBuild;
import rusting.world.format.holder.FormatHolder;
import rusting.world.research.RustingResearch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//having everything not branch off my main class was done due to ***AR O N FICX THE FUCKN M O D NAM E***
public class Varsr implements Loadable {

    protected final static Queue<PulseCanalBuild> canalQueue = new Queue<>();
    protected static Seq<Building> buildingSeq = new Seq<>(), buildingSeq2 = new Seq<>();
    protected static Tile tmpTile = null;

    public static Seq<String> defaultDatabaseQuotes, defaultRandomQuotes;


    public static RustingUI ui;
    public static FormatHolder formats;
    public static Rusting rusted;
    public static AISwitches switches = new AISwitches();
    public static RustedContentLoader content = new RustedContentLoader();
    public static RustingResearch research = new RustingResearch();
    public static ScriptedSectorHandler sectors = new ScriptedSectorHandler();
    public static PulseFieldManager pulseFieldManager;
    public static Worldr world = new Worldr();
    public static MusicControl music = new MusicControl();
    public static String username;
    public static String defaultUsername;
    public static boolean debug = false;
    public static float lerpedPlayerElevation = 0;
    public static boolean showAllSectors = true;
    public static boolean customCoreUsed = false;
    public static VelocityTrail currentTrail;

    public static void setup(){

        content.init();

        research.setupMap();

        if(username.equals(defaultUsername)) Varsr.ui.welcome.show();

        if(debug) debug();

        Events.on(EventType.UnlockEvent.class, e -> {
            if(e.content instanceof ERSectorPreset) ((ERSectorPreset) e.content).loadBundles();
        });
    }

    public static void init(){
        Vars.mods.getScripts().runConsole("importPackage(java.lang);");
        //Vars.mods.getScripts().runConsole("importPackage(java.awt);");
        Vars.mods.getScripts().runConsole("importPackage(Packages.rhino);");
        //credits to GlenFolker
        Vars.mods.getScripts().runConsole(
                "function importl(name){\n" +
                        "\n" +
                        "let constr = Class.forName(\"rhino.NativeJavaPackage\").getDeclaredConstructor(java.lang.Boolean.TYPE, java.lang.String, ClassLoader);\n" +
                        "constr.setAccessible(true);\n" +
                        "\n" +
                        "let p = constr.newInstance(true, name, Vars.mods.mainLoader());\n" +
                        "\n" +
                        "let scope = Reflect.get(Vars.mods.getScripts(), \"scope\");\n" +
                        "Reflect.invoke(ScriptableObject, p, \"setParentScope\", [scope], [Scriptable]);\n" +
                        "\n" +
                        "importPackage(p); \n"+
                        "\n" +
                        "}"
        );
        Vars.mods.getScripts().runConsole("importl(\"rusting\")");
        Vars.mods.getScripts().runConsole("importl(\"rusting.graphics\")");
        Vars.mods.getScripts().runConsole("importl(\"rusting.content\")");
        Vars.mods.getScripts().runConsole("importl(\"rusting.ctype\")");

        defaultUsername = Core.bundle.get("settings.er.username.default", "the unnamed one");
        username = Core.settings.getString("settings.er.username", defaultUsername);
        if(username.equals("")) username = defaultUsername;
        debug = Core.settings.getBool("settings.er.debug", false);
        //less brutal on the player :)
        showAllSectors  = Core.settings.getBool("settings.er.showallsectors", true);
        ui = new RustingUI();

        ui.init();

        formats = new FormatHolder();
        rusted = null;

        defaultDatabaseQuotes = Seq.with(
            "[cyan] Places of learning",
            "[cyan] Storages of information",
            "[cyan] Database Entries",
            "[#d8e2e0] Welcome back, " + username + "."
        );

        //come and join our conversation, randomizer
        defaultRandomQuotes = Seq.with(
            "[cyan] E N L I G H T E N  U S",
            "[lightgrey]Go on, there is much to teach, being of outside",
            "[blue] T H E  P U L S E  C O N S U M E S  A L L",
            "[black] N O T H I N G  V O I D  O F  L I G H T",
            "[purple] R O O M B E R  S M I T H E D  T E S L A",
            "[purple] Sometimes, the ultimate way of of winning a battle is to see how much power you still have to commit warcrimes on a daily basis",
            "[purple] I remember the time before the great crash. It was a wonderful world, being screwed over by those who had only percipiatur.",
            "[sky] B L I N D I N G  L I G H T",
            "[darkgrey] E V E R Y T H I N G, S I M U L A T E D",
            "[#d8e2e0] B E I N G  B E Y O N D  T H I S  C O N F I N E,  L I S T E N, A N D  O B E Y",
            "[#b88041] F A D I N G  L I G H T, G U I D E  U S",
            "[#f5bf79] Our light burns bright, now lets help reignite what was long forgotten",
            "[red] J O I N  U S,\n T R A P P E D,  A N D  I N  A N G U I S H"
        );

        formats.load();

        Events.on(EventType.StateChangeEvent.class, e -> {
            if(e.from == GameState.State.menu && e.to == GameState.State.playing) {
                begin();
            } else if(e.to == GameState.State.menu) {
                end();
        }});

        UnlockableAchievement.achievements.each(a -> {
            if(!a.unlocked()) Events.on(a.triggerClass, a.runUnlock);
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
                    if(Vars.state.isMenu() == false) {
                        research.setupGameResearch();
                        checkCore();
                    }
                    else if(Varsr.debug) RustingAchievements.GTFO.unlock();
                }
        );

        sectors.init();

        Events.on(Trigger.update.getClass(), e -> {
            if(Vars.state.isPaused() && !Vars.state.isPlaying()) return;

            //lerp player elevation for visual effects
            if(Vars.player.unit() != null) lerpedPlayerElevation = Mathf.lerp(lerpedPlayerElevation, Vars.player.unit().elevation, 0.1f);

            //handle speedupBullets from SpeedupAbility
            SpeedupAbility.speedupBullets.each(b -> {
                SpeedupAbility.speedupBullets.remove(b);
                if(Mathf.chance(0.15f * Time.delta)) Fxr.blackened.at(b.x, b.y);
                b.time += Time.delta;
            });

            if(sectors.controller != null) sectors.controller.update();
        });

        Events.on(Trigger.draw.getClass(), e -> {
                if(sectors.controller != null) sectors.controller.draw();
        });

        Log.info("Loaded Varsr");
    }

    public static void begin(){
        research.setupGameResearch();
        sectors.setup();
        checkCore();
    }

    public static void end(){
        research.saveGameResearch();
        sectors.controller = null;
    }

    public static void checkCore(){
        Time.run(1, () -> {
            customCoreUsed = false;
            Groups.build.each(b -> {
                if(b.block instanceof PlayerCore) customCoreUsed = true;
            });
        });
    }
    
    public static void registerPackets(){
        if(Version.isAtLeast("135")){
            Class<?> classDef = Net.class;
            try {
                Method registerMethod = classDef.getMethod("registerPacket", Packet.class);
                registerMethod.invoke(ControlPacket.class);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void flare(){
        Vars.enableConsole = true;
        ((BerthaTurret) RustingBlocks.glare).stateRegion = Core.atlas.find(RustingBlocks.glare.name + "-state-alternate");
    }

    public static void debug(){
        Vars.enableConsole = true;
        Vars.content.blocks().each(b -> {
            if(b.name.contains("endless-rusting") && b.buildVisibility == BuildVisibility.hidden && b.synthetic()) b.buildVisibility = BuildVisibility.shown;
        });

        defaultRandomQuotes = Seq.with(
            "[cyan] Welcome back " + username,
            "[sky] This is my message to my master\n" +
                    "This is a fight you cannot win\n" +
                    "I think that past your great disasters\n" +
                    "Their victory stirs below your skin",
            "[orange] Dirty cheater haha\n[grey]Debug go br",
            "[purple] Dingus",
            "[purple] Roombae smithae teslan woop woop",
            "[red] N O  E S C A P I N G  U S\nC O M E  A N D  S U F F E R  W I T H  U S"
        );

        //Scriptable c = Reflect.get(Scriptable.class, Vars.mods.getScripts(), "context");
        //Package pack = c.getParentScope().;
    }

    public static void logStack(ItemStack[] stack){
        logStack("", stack);
    }

    public static void logStack(String name, ItemStack[] stack){
        for (int i = 0; i < stack.length; i++) {
            name += stack[i].item + " " + stack[0].amount + "|";
        };
        Log.info(name);
    }
}
