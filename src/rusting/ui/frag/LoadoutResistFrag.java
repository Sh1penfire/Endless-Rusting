package rusting.ui.frag;

import arc.scene.Group;
import mindustry.graphics.Pal;
import mindustry.ui.fragments.Fragment;
import rusting.ui.dialog.Texr;

public class LoadoutResistFrag extends Fragment {

    public boolean visible = false;

    @Override
    public void build(Group parent) {
        parent.fill(table -> {
            table.name = "loadoutResist";
            table.left();
            table.table(t -> {

                t.background(Texr.button);
                t.top();
                t.image().color(Pal.heal).pad(0f).height(3f).fillX();

                t.row();
                t.add("[red]" + "WARNING\nLOADOUT RESISTANCE ACTIVE");
                t.row();

            }).size(80, 250).visible(() -> visible);
        });
    }
}
