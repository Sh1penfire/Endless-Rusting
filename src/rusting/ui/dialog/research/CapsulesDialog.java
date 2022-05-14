package rusting.ui.dialog.research;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.Dialog;
import arc.scene.ui.Image;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Time;
import mindustry.graphics.Pal;
import rusting.Varsr;
import rusting.content.Palr;
import rusting.type.Capsule;
import rusting.ui.dialog.CustomBaseDialog;

import static mindustry.Vars.mobile;

public class CapsulesDialog extends CustomBaseDialog {

    private Seq<Capsule> capsules = new Seq();

    public CapsulesDialog(){
        super(Core.bundle.get("erui.capsuledatabasepage"), Core.scene.getStyle(DialogStyle.class));
        addCloseButton();
    }
    public CapsulesDialog(String title, DialogStyle style) {
        super(title, style);
    }

    @Override
    public Dialog show() {
        setup();
        return super.show();
    }

    public void setup(){

        capsules = Varsr.content.capsules();

        capsules.each(c -> {
            if(c.hidden) capsules.remove(c);
        });

        cont.reset();
        cont.margin(20);
        cont.pane(table -> {

            table.add(Core.bundle.get("erui.capsules")).growX().left().color(Palr.lightstriken);
            table.row();
            table.image().growX().pad(5).padLeft(0).padRight(0).height(3).color(Pal.accent);
            table.row();

            table.table(list -> {

                int cols = Mathf.clamp((Core.graphics.getWidth() - 30) / (32 + 10), 1, 5);
                final int[] count = {0};

                list.left();

                capsules.each(capsule -> {

                    final boolean isResearched = capsule.unlockedNow();
                    Image nonfinalImage = new Image();
                    if(Core.atlas.isFound(capsule.uiIcon)) nonfinalImage = new Image(capsule.uiIcon).setScaling(Scaling.fit);
                    Image image = nonfinalImage.setScaling(Scaling.fit);
                    Color imageCol = isResearched ? Color.white : Pal.darkerGray;
                    list.add(image).size(8 * 12).pad(3);
                    ClickListener listener = new ClickListener();
                    image.addListener(listener);
                    if (!mobile) {
                        image.addListener(new HandCursorListener());
                        image.update(() -> image.color.lerp(!listener.isOver() ? imageCol : Color.white, Mathf.clamp(0.4f * Time.delta)));
                    }

                    boolean finalIsResearched = isResearched;
                    image.clicked(() -> {
                        //do things
                    });

                    if ((++count[0]) % cols == 0) {
                        list.row();
                    }
                });
            }).growX().left().padBottom(10);
            table.row();
        });
    }
}
