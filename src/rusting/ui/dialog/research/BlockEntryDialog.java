package rusting.ui.dialog.research;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Scaling;
import mindustry.ctype.UnlockableContent;
import mindustry.graphics.Pal;
import mindustry.world.meta.*;
import rusting.graphics.ResearchCenterUI;
import rusting.ui.dialog.CustomBaseDialog;
import rusting.world.blocks.pulse.PulseBlock;

public class BlockEntryDialog extends CustomBaseDialog {
    public BlockEntryDialog() {
        super(Core.bundle.get("erui.pulseblockpage"), Core.scene.getStyle(DialogStyle.class));
        addCloseButton();
    }

    public void show(UnlockableContent content) {

        cont.clear();

        cont.pane(table -> {

            table.margin(10);

            //initialize stats if they haven't been yet
            content.checkStats();

            table.table(title1 -> {

                TextureRegion uiIcon =
                    Core.atlas.find(content.getContentType().name() + "-" + content.name + "-ui",
                    Core.atlas.find(content.getContentType().name() + "-" + content.name + "-full",
                    Core.atlas.find(content.getContentType().name() + "-" + content.name,
                    Core.atlas.find(content.name)
                    )));

                title1.image(uiIcon).size(64).scaling(Scaling.fit);
                title1.add("[accent]" + content.localizedName).padLeft(5);
            });

            table.row();

            if (content.description != null) {
                boolean any = content.stats.toMap().size > 0;

                if (any) {
                    table.add("@category.purpose").color(Pal.accent).fillX().padTop(10);
                    table.row();
                }

                table.add("[lightgray]" + content.displayDescription()).wrap().fillX().padLeft(any ? 10 : 0).width(500f).padTop(any ? 0 : 10).left();
                table.row();

                if (!content.stats.useCategories && any) {
                    table.add("@category.general").fillX().color(Pal.accent);
                    table.row();
                }
            }

            Stats stats = content.stats;

            for (StatCat cat : stats.toMap().keys()) {
                OrderedMap<Stat, Seq<StatValue>> map = stats.toMap().get(cat);

                if (map.size == 0) continue;

                //TODO check
                if (stats.useCategories) {
                    table.add("@category." + cat.name()).color(Pal.accent).fillX();
                    table.row();
                }

                for (Stat stat : map.keys()) {
                    table.table(inset -> {
                        inset.left();
                        inset.add("[lightgray]" + stat.localized() + ":[] ").left();
                        Seq<StatValue> arr = map.get(stat);
                        for (StatValue value : arr) {
                            value.display(inset);
                            inset.add().size(10f);
                        }

                    }).fillX().padLeft(10);
                    table.row();
                }
            }

            if (content.details != null) {
                table.add("[gray]" + content.details).pad(6).padTop(20).width(400f).wrap().fillX();
                table.row();
            }

            if (content instanceof PulseBlock) {
                ResearchCenterUI.displayCustomStats(table, (PulseBlock) content);
            }

        });
        super.show();
    }

}
