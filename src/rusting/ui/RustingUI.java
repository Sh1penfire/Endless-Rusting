package rusting.ui;

import arc.*;
import arc.assets.Loadable;
import arc.scene.ui.layout.WidgetGroup;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.dialogs.LaunchLoadoutDialog;
import mindustry.ui.dialogs.PlanetDialog;
import rusting.game.ERSectorPreset;
import rusting.ui.dialog.Texr;
import rusting.ui.dialog.ingame.ERAchievementsDialog;
import rusting.ui.dialog.ingame.WelcomingDialog;
import rusting.ui.dialog.research.*;
import rusting.ui.frag.LoadoutResistFrag;

public class RustingUI implements ApplicationListener, Loadable {

    public UnlockDialog unlock;
    public BlockEntryDialog blockEntry;
    public FieldBlockListDialog blocklist;
    public ResearchDialog research;
    public CapsulesDialog capsuleResearch;
    public WelcomingDialog welcome;
    public ERAchievementsDialog achievements;

    public LoadoutResistFrag loadoutResistance;

    public WidgetGroup planetsUI;
    private PlanetDialog planet;
    private LaunchLoadoutDialog loadout;

    @Override
    public void init(){
        Texr.load();
        unlock = new UnlockDialog();
        blockEntry = new BlockEntryDialog();
        blocklist = new FieldBlockListDialog();
        research = new ResearchDialog();
        capsuleResearch = new CapsulesDialog();
        welcome = new WelcomingDialog();
        achievements = new ERAchievementsDialog();
        loadoutResistance = new LoadoutResistFrag();
        planetsUI = new WidgetGroup();

        Core.scene.add(planetsUI);        Events.on(EventType.ClientLoadEvent.class, e -> {
            Core.app.post(() -> {
                loadoutResistance.build(Vars.ui.hudGroup);
            });
        });

        Vars.ui.planet.loadouts.shown(() -> {
            updateResistTable();
        });

        Vars.ui.planet.loadouts.hidden(() -> {
            loadoutResistance.visible = false;
        });

    }

    public void updateResistTable(){
        planet = Vars.ui.planet;
        loadoutResistance.visible = (planet.selected.preset != null && planet.selected.preset instanceof ERSectorPreset);
    }
}
