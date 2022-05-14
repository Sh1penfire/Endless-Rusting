package rusting.ui;

import arc.ApplicationListener;
import arc.assets.Loadable;
import mindustry.Vars;
import rusting.ui.dialog.Texr;
import rusting.ui.dialog.ingame.ERAchievementsDialog;
import rusting.ui.dialog.ingame.WelcomingDialog;
import rusting.ui.dialog.research.*;
import rusting.ui.frag.UnitSelectFrag;

public class RustingUI implements ApplicationListener, Loadable {

    public BlockEntryDialog blockEntry;
    public FieldBlockListDialog blocklist;
    public ResearchDialog research;
    public WelcomingDialog welcome;
    public ERAchievementsDialog achievements;

    public UnitSelectFrag unitSelect;


    @Override
    public void init(){
        Texr.load();
        blockEntry = new BlockEntryDialog();
        blocklist = new FieldBlockListDialog();
        research = new ResearchDialog();
        welcome = new WelcomingDialog();
        achievements = new ERAchievementsDialog();
        unitSelect = new UnitSelectFrag();

        unitSelect.build(Vars.ui.hudGroup);

    }
}
