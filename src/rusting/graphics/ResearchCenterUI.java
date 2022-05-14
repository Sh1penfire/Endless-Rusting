package rusting.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.graphics.Pal;
import rusting.content.RustingBlocks;
import rusting.interfaces.ResearchableObject;
import rusting.world.blocks.pulse.PulseBlock;

public class ResearchCenterUI {

    public static String getQuote(PulseBlock content, Seq<String> randomQuotes){
        return content == RustingBlocks.pulseTesla && randomQuotes.size > 5 ? randomQuotes.get(4) : randomQuotes.random();
    }

    public static void displayCustomStats(Table table, ResearchableObject content) {
            //iterates over all of the loadable StatGroups in the CustomStatHolder
            content.customStatHolder().loadableGroups.each(l -> {
            table.table(t -> {
                //if the StatGroup can load it's stats continue
                if(l.canLoadStats()) {
                    //Adds a header for the StatGroup
                    t.add(Core.bundle.get("pulsecategory." + l.header())).color(Color.sky.lerp(Pal.lancerLaser, 0.10f)).fillX();
                    t.row();

                    Log.info(Core.bundle.get("pulsecategory." + l.header()));

                    //iterates over all stats for this StatGroup
                    l.stats.each(s -> {
                        //If the stat has been assigned a value, add a Label to the table
                        if (s.hasValue()) formatTable(table, "pulsefield." + s.name, s.getValue(), "pulsefieldsufix." + s.suffix, 6, 3);
                        Log.info(s.getValue());
                        Log.info(s.name);
                    });
                }
            });
        });
    }

    private static void formatTable(Table table, String bundleID, Object stats, String sufixBundleID, int pad, int padTop) {
        table.add(
                String.format(
                        "%s: %s",
                        Core.bundle.get(bundleID),
                        stats
                ) + Core.bundle.get(sufixBundleID)
        ).pad(pad).padTop(padTop)
                .width(450f)
                .wrap()
                .fillX();
        table.row();
    }
}
