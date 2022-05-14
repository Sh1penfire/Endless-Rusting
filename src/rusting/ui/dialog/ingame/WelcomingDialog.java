package rusting.ui.dialog.ingame;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.*;
import arc.scene.ui.layout.Cell;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import rusting.Varsr;
import rusting.ui.dialog.CustomBaseDialog;

public class WelcomingDialog extends CustomBaseDialog {

    private String name = "";
    private Cell button;
    private Cell savedTable;
    private boolean enableSkip = false;

    public WelcomingDialog() {
        super(Core.bundle.get("erui.welcomepage"), Core.scene.getStyle(DialogStyle.class), false);
    }

    public void setup(){
        cont.clear();

        if(!Vars.player.name().equals("")) enableSkip = true;

        cont.table(table -> {
            if(Version.number > 6){
                table.add(new Image(Fonts.getGlyph(mindustry.ui.Fonts.def, (char)9888).getRegion())).size(64, 64).color(Pal.health);
                table.row();
                table.add(Core.bundle.get("erui.v7warning"));
                table.row();
            }
            else{
                table.add(new Image(Core.atlas.find("endless-rusting-icon"))).size(64, 64);
            }
        }).center().top().padTop(15);
        cont.row();

        cont.pane(table -> {

            table.add(Core.bundle.format("erui.greeting", Vars.player.name().equals("") ? Core.bundle.get("settings.er.username.default") : Vars.player.name())).padTop(30);
            table.row();

            name = Vars.player.name();
            table.table(t -> {
                t.add("@name").padRight(10);
                t.field(Core.settings.getString("name"), text -> {
                    name = text;
                }).pad(8);//.addInputDialog(Vars.maxNameLength);
            });
            table.row();

            button = table.button(Core.bundle.get("erui.button.confirm"), () -> {
                if(name.equals("")) return;
                Image clearButton = new Image(Core.atlas.find("clear"));
                button.setElement(clearButton);
                savedTable.clearElement();
                Varsr.username = name;
                Core.settings.put("settings.er.username", name);
                if(!enableSkip) {
                    Vars.player.name(name);
                    Core.settings.put("name", name);
                }
                table.row();
                String sendOff = Core.bundle.get("erui.defaultsendoff");
                if(name.equals("Sh1penfire")){
                    Core.settings.put("settings.er.debug", true);
                    sendOff = Core.bundle.get("erui.sh1penfire");
                    Varsr.debug = true;
                    Varsr.debug();
                }
                else{
                    Core.settings.put("settings.er.debug", false);
                    Varsr.debug = false;
                }
                table.add(Core.bundle.format("erui.aftergreeting", name, sendOff));

                buttons.defaults().size(210f, 64f);
                buttons.button("@back", Icon.left, this::hide).size(210f, 64f);

                addCloseListener();

            }).grow().padTop(15);
        });

        cont.row();

        savedTable = cont.table(t -> {

            if(enableSkip) {
                t.button(Core.atlas.drawable("check-on"), () -> {
                    hide();
                });
            }
            else {
                Image button = new Image(Core.atlas.drawable("check-on-disabled"));
                ClickListener listener = new ClickListener();
                button.addListener(listener);
                if(!Vars.mobile){
                    button.addListener(new HandCursorListener());
                    button.update(() -> {
                        button.color.lerp(!listener.isOver() ? Color.white : Pal.darkerMetal, Mathf.clamp(0.4f * Time.delta));
                    });

                    button.addListener(new Tooltip(tp -> tp.background(Core.atlas.drawable("clear")).add((Core.bundle.get("erui.skipcheckdisabled")))));
                }

                t.add(button);
            }

            t.add((enableSkip ? "[white]" : "[grey]") + Core.bundle.format("erui.button.skipparam", Core.bundle.get("erui.button.skipbutton"))).padLeft(6);
        }).bottom().right();
    }

    @Override
    public Dialog show() {
        setup();
        return super.show();
    }
}
