package rusting.ui;

import arc.ApplicationListener;
import arc.assets.Loadable;
import rusting.ui.dialog.Texr;
import rusting.ui.dialog.ingame.ERAchievementsDialog;
import rusting.ui.dialog.ingame.WelcomingDialog;
import rusting.ui.dialog.research.*;

public class RustingUI implements ApplicationListener, Loadable {
    public UnlockDialog unlock;
    public BlockEntryDialog blockEntry;
    public FieldBlockListDialog blocklist;
    public ResearchDialog research;
    public CapsulesDialog capsuleResearch;
    public WelcomingDialog welcome;
    public ERAchievementsDialog achievements;

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
    }
}
