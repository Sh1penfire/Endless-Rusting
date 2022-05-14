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

let bar = new Bar(() => respawning, () => getBarCol(), () => !visibility.get() ? 1 : (core() == null ? 0 : Interp.fade.apply(currentCore.progressf())));

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

Events.run(Trigger.update, () => {
    Groups.unit.each(u =>{
        if(!u.type != UnitTypes.beta)
            return;
        u.mounts.forEach(w => {
            let target = Damage.linecast(b, b.x, b.y, b.rotation(), length);
            if(target != null) Tmp.v1.set(target.pos()).sub(u.x, u.y);
            else Tmp.v1.trns(w.rotation, 45);
            Drawf.light(u.team, weaponPos.x, weaponPos.y, weaponPos.x + Tmp.v1.x, weaponPos.y + Tmp.v1.y, weaponPos.dst(Tmp.v1), u.type.lightColor, u.type.lightOpacity);
        });
    });
});

let trailObject = {
    length: 40,
    pointSpacing: 1,
    maxPointSpacing: 2.5,
    maxSpeed: 3,
    tension: 0.25,
    lerpSpeed: 0.05,
    continuation: 0.1,
    segPoses: [],
    newPoses: [],
    segVelocity: [],
    init(){
        for(let i = 0; i < this.length; i++){
            this.segPoses.push(new Vec2(0, 0));
            this.newPoses.push(new Vec2(0, 0));
            this.segVelocity.push(new Vec2(0, 0));
        }
        this.segPoses[this.length] = this.segPoses[this.length - 1];
    },
    update(targetPos, velocity){
        this.segPoses[0] = targetPos;
        this.segVelocity[0] = velocity;
        for(let i = 1; i < this.length - 1; i++){
            let segment = this.segPoses[i];
            let previous = this.segPoses[i - 1];
            let previousVel = this.segVelocity[i - 1];
            
            let distanceVec = new Vec2();
            let newPos = this.newPoses[i];
            distanceVec.trns(segment.angleTo(previous), Math.min((segment.dst(previous) - this.pointSpacing) * this.tension, this.maxSpeed));
            this.segVelocity[i].lerp(distanceVec, this.lerpSpeed).lerp(previousVel, this.continuation);
            newPos.set(segment).add(this.segVelocity[i]);
        }
        for(let i = 1; i < this.length - 1; i++){
            let segment = this.segPoses[i];
            let previous = this.segPoses[i - 1];
            let newPos = this.newPoses[i];
            segment.set(newPos);
            segment.sub(previous).clamp(0, this.maxPointSpacing).add(previous);
        }
    },
    draw(){
        for(let i = 0; i < this.length - 1; i++){
            let segment = this.segPoses[i];
            let velocity = this.segVelocity[i];
            Fill.circle(segment.x, segment.y, 4);
            Lines.line(segment.x, segment.y, Tmp.v1.set(segment).add(Tmp.v2.set(velocity).scl(10)).x, Tmp.v1.y);
        }
    }
    
};
trailObject.init();
function currentTrail(){return trailObject;};
Events.run(Trigger.update, () => currentTrail().update(Vars.player.unit(), Vars.player.unit().vel));
Events.run(Trigger.draw, () => currentTrail().draw());
*/