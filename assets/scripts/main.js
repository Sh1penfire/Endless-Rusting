Events.on(ClientLoadEvent,
    e => {
        let unit = Vars.content.units().find(u => u.name == "endless-rusting-guardian-sulphur-stingray");
        Log.info("hai");
        if(Version.number > 6 && !Vars.headless){
            unit.omniMovement = true;
            unit.faceTarget = false;
            unit.targetFlags = [BlockFlag.turret, null];
            
            Vars.content.sectors().each(s => {
                if(s.minfo.mod != null && s.minfo.mod.name == "endless-rusting" && s.icon != Core.atlas.getDrawable("error")){
                    s.uiIcon = s.fullIcon = s.icon.getRegion();
                }
            });
        }
        else unit.targetFlag = BlockFlag.turret;
    }
);

/*
let button = Version.number >= 7 ? Core.atlas.getDrawable("button") : Tex.button;

let buttonOver = Version.number >= 7 ? Core.atlas.getDrawable("check-over") : Tex.buttonOver;

let pause = Core.atlas.getDrawable("pause");

let currentCore = null;

function core(){
  currentCore = Groups.build.find(b => {
      return b.block.name.equals("endless-rusting-crae-core");
  });
    return currentCore;
};

let respawning = "respawning";

let bar = new Bar(() => respawning, () => getBarCol(), () => !visibility.get() ? 1 : (core() == null ? 0 : Interp.fade.apply(currentCore.progress/currentCore.block.constructTime)));

function getBarCol(){
    if(core() != null && currentCore.paused.contains(Vars.player.id)) return Pal.health;
    else return visibility.get() ? Pal.accent : Pal.heal;
};

let respawnTable = extend(Table, {

});

let respawnCell = null;
let buttonCell = null;
let previouslyShown = false;
let container = null;

let visibility = boolp(() => (Vars.player.unit() == null || Vars.player.unit().isNull()));

function dots(){
    for(let i = 0; i < Mathf.ceil(Time.time/25) % 3 + 1; i++){
        respawning += ".";
    }
};

let shownState = {
    transitionTo: null,
    shouldTransition(){
        return !visibility.get();
    },
    onTransition(){
        container.visible = true;
        container.actions(
            Actions.alpha(0),
            Actions.delay(0.25),
            Actions.fadeIn(1, Interp.fade)
        );
    },
    update(){
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
    },
    toString(){
        return "State: Shown";
    }
}

let hiddenState = {
    transitionTo: shownState,
    shouldTransition(){
        return visibility.get();
    },
    onTransition(){
        respawning = "Done!";
        container.actions(
            Actions.fadeOut(0.35, Interp.fade),
            Actions.alpha(0),
            Actions.hide()
        );
    },
    update(){},
    toString(){
        return "State: Hidden";
    }
}

let visibilityState = hiddenState;

shownState.transitionTo = hiddenState;


respawnTable.background(button);
respawnTable.add(bar).growX().height(18.0).pad(4);

let fragAlpha = 0;

let pauseButton = new ImageButton(pause);

pauseButton.clicked(() => {
    if(core() != null) currentCore.pause(Vars.player.id);
});

let frag = extend(Fragment, {
    build(parent){
        parent.fill(cons(c => {
            container = c;
            this.rebuild();
        }));
    },
    rebuild(){
        container.clear();
        container.name = "menu container";
        respawnCell = container.add(respawnTable).center().height(100).width(350).get();
        buttonCell = container.add(pauseButton).right().size(100);
    }
});
frag.build(Vars.ui.hudGroup);

Events.run(Trigger.update, () => {
    if(visibilityState.shouldTransition()){
        visibilityState = visibilityState.transitionTo;
        visibilityState.onTransition();
    }
    visibilityState.update();
});
*/