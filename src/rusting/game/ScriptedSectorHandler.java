package rusting.game;

import arc.Core;
import arc.Events;
import arc.assets.Loadable;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.io.JsonIO;
import rusting.Varsr;

//Thanks Glen, going to be fun playing around with this :D
public class ScriptedSectorHandler implements Loadable {

    //sector it's attached to
    private ScriptedSector sector;

    //whether currently handling a sector
    private boolean attached = false;

    //variable for storing current set of event nodes
    public Seq<SectorEventNode> nodes = new Seq();

    public ScriptedSectorHandler(){
        Events.on(EventType.ClientLoadEvent.class, e -> {
            registerEvents();
        });
    }

    protected void registerEvents(){
        /*
        Events.on(EventType.StateChangeEvent.class, e -> {
            if(Vars.state.isEditor()){
                if(e.from == State.menu && e.to == State.playing){
                    begin();
                }
                else end();
            }
        });

         */
    }

    public void begin(){

    }

    public void end(){
        try{
            Vars.ui.editor.editor.tags.put("er.sectorevents", JsonIO.json.toJson(nodes, Seq.class));
            nodes.clear();
        }
        catch (Error e){
            Vars.ui.showException(Varsr.username + ", "+  Core.bundle.get("er.failsave"), e);
        }
    }

    public void update(){

    }

    public void draw(){

    }


}
