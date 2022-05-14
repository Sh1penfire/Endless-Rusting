package rusting.ui.dialog;

import arc.Core;
import arc.scene.ui.Dialog;
import arc.util.Align;
import mindustry.core.GameState.State;
import mindustry.gen.*;
import mindustry.graphics.Pal;

import static mindustry.Vars.net;
import static mindustry.Vars.state;

//preparing for v7 so that I can adjust for v7's Base Dialog differences
public class CustomBaseDialog extends Dialog {
    private boolean wasPaused;
    protected boolean shouldPause;

    public CustomBaseDialog(String title, DialogStyle style){
        this(title, style, true);
    }

    public CustomBaseDialog(String title, DialogStyle style, Boolean shouldHeader){
        super(title, style);
        setFillParent(true);
        if(shouldHeader){
            this.title.setAlignment(Align.center);
            titleTable.row();
            titleTable.image(
                arc.Core.atlas.drawable("whiteui"),
                Pal.accent)
            .growX().height(3f).pad(4f);
        }

        hidden(() -> {
            if(shouldPause && state.isGame()){
                if(!wasPaused || net.active()){
                    state.set(State.playing);
                }
            }
            Sounds.back.play();
        });

        shown(() -> {
            if(shouldPause && state.isGame()){
                wasPaused = state.is(State.paused);
                state.set(State.paused);
            }
        });
    }

    public CustomBaseDialog(String title){
        this(title, Core.scene.getStyle(DialogStyle.class));
    }

    /*
    protected void onResize(Runnable run){
        resized(run);
    }*/

    public void addCloseListener(){
        closeOnBack();
    }

    @Override
    public void addCloseButton(){
        buttons.defaults().size(210f, 64f);
        buttons.button("@back", Icon.left, this::hide).size(210f, 64f);

        addCloseListener();
    }
}
