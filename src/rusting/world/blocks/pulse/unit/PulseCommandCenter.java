package rusting.world.blocks.pulse.unit;

import arc.graphics.Color;
import arc.scene.event.ClickListener;
import arc.scene.event.HandCursorListener;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Sounds;
import mindustry.ui.Cicon;
import rusting.Varsr;
import rusting.ai.AISwitches.AISwitch;
import rusting.ai.AISwitches.AiSwitchHolder;
import rusting.ui.dialog.Texr;
import rusting.world.blocks.pulse.PulseBlock;

import static mindustry.Vars.mobile;

public class PulseCommandCenter extends PulseBlock {
    public static PulseCommandCenterBuild currentCenter;
    public static Table unitsTable = new Table();

    public PulseCommandCenter(String name) {

        super(name);
        configurable = true;

    }

    public class PulseCommandCenterBuild extends PulseBlockBuild{
        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);
            currentCenter = this;
            unitsTable.clear();

            Vars.content.units().each(type -> {
                if(!Varsr.switches.map.containsKey(type)) return;
                Seq<AISwitch> switches = Varsr.switches.map.get(type);
                unitsTable.table(t -> {
                    Image unit = new Image(type.icon(Cicon.medium));
                    t.add(unit).size(64);
                    switches.each(AISwitchType -> {

                        AiSwitchHolder AISwitch = Varsr.switches.getSwitchHolder(type, Vars.player.team(), AISwitchType);

                        Image image = new Image(AISwitchType.icon).setScaling(Scaling.fit);
                        image.color.set(AISwitch.isOn ? Color.white : Color.gray);

                        t.add(image).size(64).pad(5);

                        ClickListener listener = new ClickListener();
                        image.addListener(listener);

                        image.addListener(new Tooltip(tip -> {
                            tip.background(Texr.button).add(AISwitchType.localizedName);
                        }));

                        if (!mobile) {
                            image.addListener(new HandCursorListener());
                            image.update(() -> image.color.lerp((AISwitch.isOn ? Color.white : Color.gray), 0.05f * Time.delta));
                        }

                        image.clicked(() -> {
                            AISwitch.isOn = !AISwitch.isOn;
                            Sounds.buttonClick.play();
                        });

                    });
                    t.setBackground(Texr.button);
                });
                unitsTable.row();
            });
            table.add(unitsTable);
        }
    }
}
