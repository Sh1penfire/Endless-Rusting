package rusting.ui.dialog.research;

import arc.Core;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Image;
import arc.scene.ui.Tooltip;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Cicon;
import mindustry.ui.Fonts;
import mindustry.world.Tile;
import rusting.Varsr;
import rusting.content.Palr;
import rusting.ctype.ResearchType;
import rusting.interfaces.ResearchCenter;
import rusting.interfaces.ResearchableObject;
import rusting.ui.dialog.CustomBaseDialog;
import rusting.ui.dialog.Texr;

import static mindustry.Vars.*;

public class FieldBlockListDialog extends CustomBaseDialog {

    public Seq<ResearchableObject> researchable = new Seq<ResearchableObject>();
    public Seq<String> databaseQuotes = new Seq<String>();

    public FieldBlockListDialog() {
        super(Core.bundle.get("erui.pulseblockdatabasepage"), Core.scene.getStyle(DialogStyle.class));
        addCloseButton();
    }

    public void makeList(Seq<ResearchType> researchTypes){
        researchable.clear();
        researchTypes.each(type -> {
            Varsr.research.researchMap.get(type).each(m -> {
                researchable.add(m.item);
            });
        });
    }

    public void refresh(Tile tile){
        if(isShown()) {
            hide();
            show(tile);
        }
        else if(tile.build != null && tile.build.block instanceof ResearchCenter) makeList(((ResearchCenter) tile.build.block).researchTypes());
    }

    /*
    public void makeList(Seq<String> fieldNames, int threshold) {
        researchable.clear();
        Vars.content.blocks().each(b -> {
            int fields = 0;
            for (String field : fieldNames) {
                try {
                    b.getClass().getField(field);
                    fields++;
                } catch (NoSuchFieldException ignored) {}
            }

            if (fields >= 1 && fields >= threshold && b instanceof ResearchableObject) researchable.add((ResearchableObject) b);
        });
    }

     */

    public void show(Tile tile) {
        if(!(tile.build instanceof Building && tile.build.block instanceof ResearchCenter)) return;
        makeList(((ResearchCenter) tile.build.block).researchTypes());
        setup();
        super.show();
    }

    public void setup(){

        cont.reset();
        cont.margin(20);
        cont.pane(table -> {

            table.add(databaseQuotes.random()).growX().left().color(Palr.pulseChargeEnd);
            table.row();
            table.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
            table.row();

            table.table(list -> {

                int cols = Mathf.clamp((Core.graphics.getWidth() - 30) / (32 + 10), 1, 5);
                final int[] count = {0};

                list.left();

                researchable.each(type -> {
                    if(!(type instanceof UnlockableContent) || Varsr.research.getCenter(type.researchTypes(), player.team()) == null) return;
                    UnlockableContent unlock = (UnlockableContent) type;
                    if (!unlocked(unlock) || type.getResearchModule().isHidden) return;
                    final boolean isResearched = Varsr.research.researched(player.team(), type, type.researchTypes());
                    Image image = new Image(unlock.icon(Cicon.medium)).setScaling(Scaling.fit);
                    Color imageCol = isResearched ? Color.white : Pal.darkerGray;
                    list.add(image).size(8 * 12).pad(3);
                    ClickListener listener = new ClickListener();
                    image.addListener(listener);
                    if (!mobile) {
                        image.addListener(new HandCursorListener());
                        image.update(() -> image.color.lerp(!listener.isOver() ? imageCol : Palr.pulseChargeEnd, Mathf.clamp(0.4f * Time.delta)));
                    }

                    boolean finalIsResearched = isResearched;
                    image.clicked(() -> {
                        if (isResearched) {
                            if (Core.input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(unlock.name) != 0) {
                                Core.app.setClipboardText((char) Fonts.getUnicode(unlock.name) + "");
                                Vars.ui.showInfoFade("@copied");
                            } else Varsr.ui.blockEntry.show(unlock);
                        } else if (unlocked(unlock)) Varsr.ui.unlock.show(unlock);
                    });
                    boolean finalIsResearched1 = isResearched;
                    image.addListener(new Tooltip(t -> {
                        t.background(Texr.button).add((finalIsResearched1 ? "The " : "Unlock the ") + unlock.localizedName + (finalIsResearched1 ? "" : "?"));
                        t.row();
                    }));

                    if ((++count[0]) % cols == 0) {
                        list.row();
                    }
                });
            }).growX().left().padBottom(10);
            table.row();
        });
    }

    private boolean unlocked(UnlockableContent content){
        return (!Vars.state.isCampaign() && !Vars.state.isMenu()) || content.unlocked();
    }

}
