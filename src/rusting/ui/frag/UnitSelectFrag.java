package rusting.ui.frag;

import arc.Core;
import arc.Events;
import arc.func.Boolp;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.style.Drawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Groups;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.ui.fragments.Fragment;
import rusting.Varsr;
import rusting.world.blocks.PlayerCore;
import rusting.world.blocks.PlayerCore.PlayerCoreBuild;

public class UnitSelectFrag extends Fragment {
    public Boolp visibility = () -> (Vars.player.unit() == null || Vars.player.unit().isNull()) && Varsr.customCoreUsed;
    public Table container;

    Cell respawnCell = null, buttonCell = null;
    public VisibilityState shownState, hiddenState;
    VisibilityState visibilityState;

    Drawable button = Version.number >= 7 ? Core.atlas.getDrawable("button") : Tex.button,
    buttonOver = Version.number >= 7 ? Core.atlas.getDrawable("check-over") : Tex.buttonOver,
    pause = Core.atlas.getDrawable("pause");

    PlayerCoreBuild currentCore = null;

    //Whats displayed on the bar
    String respawning = "respawning";

    Bar bar = new Bar(() -> respawning, () -> getBarCol(), () -> !visibility.get() ? 1 : (core() == null ? 0 : Interp.fade.apply(currentCore.progressf())));

    Table pauseButton = new ImageButton(pause);

    Table respawnTable = new Table();

    public void dots(){
        for(int i = 0; i < Mathf.ceil(Time.time/25) % 3 + 1; i++){
            respawning += ".";
        }
    };

    Color getBarCol(){
        if(core() != null && currentCore.paused.contains(Vars.player.id)) return Pal.health;
        return visibility.get() ? Pal.accent : Pal.heal;
    };

    public PlayerCoreBuild core(){
        currentCore = (PlayerCoreBuild) Groups.build.find(b -> b.block instanceof PlayerCore);
        return currentCore;
    };

    @Override
    public void build(Group parent) {
        shownState = new VisibilityState() {
            public VisibilityState transitionTo(){
                return hiddenState;
            }
            public boolean shouldTransition(){
                return !visibility.get();
            }
            public void onTransition(){

            }

            @Override
            public void onStart() {
                container.visible = true;
                container.actions(
                        Actions.alpha(0),
                        Actions.delay(0.25f),
                        Actions.fadeIn(1, Interp.fade)
                );
            }

            public void update(){
                currentCore = core();
                if(currentCore == null){
                    respawning = "Waiting on core to respond";
                    return;
                }
                if(currentCore.que.size > 0 && currentCore.que.get(0) == Vars.player && !currentCore.paused.contains(Vars.player.id)){
                    respawning = "Respawning";
                }
                else{
                    respawning = currentCore.que.size > 1 ? "Waiting on another player" : "Waiting";
                }
                dots();
            }
            public String toString(){
                return "State: Shown";
            }
        };

        hiddenState = new VisibilityState() {
            public VisibilityState transitionTo(){
                return shownState;
            }
            public boolean shouldTransition(){
                return visibility.get();
            }
            public void onStart(){
                respawning = "Done!";
                container.actions(
                        Actions.fadeOut(0.35f, Interp.fade),
                        Actions.alpha(0),
                        Actions.hide()
                );
            }

            @Override
            public void onTransition() {

            }

            public void update(){

            }
            public String toString(){
                return "State: Hidden";
            }
        };

        parent.fill(c -> {
            c.name = "select container";
            c.visible(visibility);
            container = c;

            if(!Vars.mobile) buildDesktop(parent);
            else buildMoble(parent);
        });


        pauseButton.clicked(() -> {
            if(core() != null) currentCore.pause((byte) Vars.player.id);
        });

        respawnTable.background(button);
        respawnTable.add(bar).growX().height(18).pad(4);

        visibilityState = hiddenState;

        Events.run(Trigger.update, () -> {
            if(visibilityState.shouldTransition()){
                visibilityState = visibilityState.transitionTo();
                visibilityState.onStart();
            }
            visibilityState.update();
        });
    }

    public void rebuild(){
    }

    public void buildDesktop(Group parent){
        visibilityState = hiddenState;
        container.clear();
        container.name = "menu container";
        container.add(respawnTable).center().height(100).width(350);
        container.add(pauseButton).right().size(100);
    }

    public void buildMoble(Group parent){

    }

    //This interface is a state variable which controls the table along with representing the current state.
    public interface VisibilityState{
        boolean shouldTransition();
        VisibilityState transitionTo();
        void onTransition();
        void onStart();
        void update();
    }
}
