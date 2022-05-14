package rusting.content;

import arc.Core;
import arc.scene.Group;
import arc.scene.ui.Dialog;
import arc.scene.ui.TextButton.TextButtonStyle;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.ui.Styles;
import rusting.ui.dialog.CustomBaseDialog;

public class RustedSettingAdder {

    public static Dialog ERSettings;
    //public static SettingsTable graphics;

    public void init(){

        ERSettings = new CustomBaseDialog("@settings.er.settings.name");
        ERSettings.addCloseButton();
        /*
        boolean tmp = Core.settings.getBool("uiscalechanged", false);
        Core.settings.put("uiscalechanged", false);

        addGraphicSetting("settings.er.drawtrails");
        addGraphicSetting("settings.er.advancedeffects");
        addGraphicSetting("settings.er.weatherblinding");
        addGraphicSetting("settings.er.pulsehighdraw");
        addGraphicSetting("settings.er.pulsedrawshake");
        addGraphicSetting("settings.er.additivepulsecolours");
        addGraphicSetting("settings.er.pulseglare");

        Core.settings.put("uiscalechanged", tmp);

        Cons dialogShow = new Cons() {
            
            public void get(Object o) {
                if(Core.input.keyTap(KeyCode.shiftLeft)) {
                    Varsr.ui.capsuleResearch.show();
                    Varsr.content.capsules().each(c -> {
                        Log.info(c.name + (Core.atlas.isFound(c.uiIcon) && c.uiIcon != null ? " has a ui region" : " does not have a ui icon"));
                    });
                }
            }
        };

        Events.on(Trigger.update.getClass(), dialogShow);

        */
        TextButtonStyle style = Styles.cleart;

        Vars.ui.settings.shown(() -> {
            rebuildMenu();
            Table settingDialog = (Table)((Group)((Group)(Vars.ui.settings.getChildren().get(1))).getChildren().get(0)).getChildren().get(0);
            settingDialog.row();
            settingDialog.button("@settings.er", style, () -> {
                ERSettings.show();
            });
        });

        rebuildMenu();

        //I literaly gave up and coppied meep after about an hour of testing this crap
        //Table settingDialog = (Table) (((Table) (((ScrollPane) Vars.ui.settings.getCells().get(3).get()).getWidget())).getCells().get(0).get());
    }

    public void rebuildMenu() {
        ERSettings.cont.clear();

        ERSettings.cont.pane(t -> {
            t.image(Core.atlas.find("endless-rusting-ingame-icon"));
            t.row();
            t.add("So... you found ER's menue for the first time! Welcome to the menue.\nThat's about all I had to say... yeah");
            t.row();
            //t.add(graphics).padTop(100);
        });

        ERSettings.cont.row();
        /*
        graphics = new SettingsTable();

        graphics.checkPref("settings.er.drawtrails", true);
        graphics.checkPref("settings.er.advancedeffects", true);
        graphics.checkPref("settings.er.weatherblinding", true);
        graphics.checkPref("settings.er.pulsehighdraw", true);
        graphics.checkPref("settings.er.pulsedrawshake", true);
        graphics.checkPref("settings.er.additivepulsecolours", true);
        graphics.checkPref("settings.er.pulseglare", true);

         */
    }
}
