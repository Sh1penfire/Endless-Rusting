package rusting.ui.frag;

import arc.func.Boolp;
import arc.scene.Group;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.ui.fragments.Fragment;
import rusting.Varsr;

public class UnitSelectFrag extends Fragment {
    public Boolp visibility = () -> (Vars.player.unit() == null || Vars.player.unit().isNull()) && Varsr.customCoreUsed && Vars.ui.hudfrag.shown;
    public Table container;

    @Override
    public void build(Group parent) {
        parent.fill(c -> {
            c.name = "select container";
            //c.visible(visibility);
            container = c;

            if(!Vars.mobile) buildDesktop(parent);
            else buildMoble(parent);
        });
    }

    public void buildDesktop(Group parent){
        container.add("REEEEEEEEEEEEEEEEEE");
    }
    public void buildMoble(Group parent){

    }
}
