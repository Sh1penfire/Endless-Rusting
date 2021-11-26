package rusting.ui.dialog.research;

import arc.Core;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.type.ItemStack;
import mindustry.ui.Cicon;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import rusting.Varsr;
import rusting.interfaces.ResearchableObject;
import rusting.ui.dialog.CustomBaseDialog;

import static mindustry.Vars.player;

public class UnlockDialog extends CustomBaseDialog {

    public TextureRegionDrawable unlockIcon = new TextureRegionDrawable();
    public Image unlockImage = new Image();

    public UnlockDialog() {
        super(Core.bundle.get("erui.unlockpage"), Core.scene.getStyle(DialogStyle.class));
    }

    public void show(UnlockableContent content){

        clear();
        addCloseButton();
        Tile tile = Varsr.research.getCenter(((ResearchableObject) content).researchTypes(), player.team()).tile;

        cont.margin(30);
        unlockIcon.set(content.icon(Cicon.tiny));
        unlockImage = new Image(unlockIcon).setScaling(Scaling.fit);
        ItemStack[] rCost = ((ResearchableObject) content).getResearchModule().centerResearchRequirements;
        Table itemsCost = new Table();

        itemsCost.table(table -> {

            //used for columns.
            int count = 1;
            int cols = Mathf.clamp((Core.graphics.getWidth() - 30) / (32 + 10), 1, 8);

            for(ItemStack costing: rCost) {
                Image itemImage = new Image(new TextureRegionDrawable().set(costing.item.icon(Cicon.medium))).setScaling(Scaling.fit);

                table.stack(
                    itemImage,
                    new Table(t -> {
                        t.add(Math.min(tile.build.team.core().items.get(costing.item), costing.amount) + "/" + costing.amount);
                    }).left().margin(1, 3, 2, 0)
                ).pad(10f);
                if((count++) % cols == 0) table.row();
            }
        });
        pane(table -> {
            table.center();
            table.button("Unlock?", () -> {
                if(content instanceof ResearchableObject){
                    Building building = tile.build;
                    CoreBuild coreBlock = building.team.core();
                    boolean canResearch = false;

                    //if it's infinite resources or the core has the resources available, continue
                    if(Vars.state.rules.infiniteResources || coreBlock.items.has(rCost, 1)){

                        //remove items from core
                        for(int i = 0; i < ((ResearchableObject) content).getResearchModule().centerResearchRequirements.length; i++){
                            coreBlock.items.remove(((ResearchableObject) content).getResearchModule().centerResearchRequirements[i]);
                        }

                        //research the block
                        building.configure(((ResearchableObject) content).name());
                        Sounds.unlock.at(player.x, player.y);
                    }
                }
                Varsr.ui.blocklist.refresh(tile);
                hide();
            }).height(75f).width(145);
            table.add(unlockImage).size(8 * 12);
            table.row();
            table.add(itemsCost);
        });

        super.show();
    }
}
