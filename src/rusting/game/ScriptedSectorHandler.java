package rusting.game;

import arc.Core;
import arc.assets.Loadable;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.io.JsonIO;
import rusting.Varsr;
import rusting.game.nodes.EventNode;

//Thanks Glen, going to be fun playing around with this :D
public class ScriptedSectorHandler implements Loadable {

    //if the handler is active or not
    public boolean active = false;

    //used  as a temporarily variable in writing the nodes to the editor's tags
    public static String completeEntry;


    //nodes currently in the sector
    public Seq<EventNode> nodes = Seq.with();
    //nodes which are active in the sector
    public Seq<EventNode> activeNodes = Seq.with();

    //used for itterating over everything without fucking activeNodes over
    private Seq<EventNode> activeNodesClone;

    //register custom nodes to strings, why am I making this it's not like some mod is going to use ER as a dependanc-
    public ObjectMap<String, NodeSupplier> nodeRegistery = ObjectMap.of();

    //sector it's attached to
    private ScriptedSector sector;

    //placeholder variable for if it should save
    private boolean save = false;

    public ScriptedSectorHandler(){
        
    }

    public NodeSupplier getNode(String key){
        return nodeRegistery.get(key);
    }

    public void readNodes() {
        String json = Vars.state.rules.tags.get("er.sectorevents", "");
        if (json == "") {
            active = false;
            return;
        };
        try {
            Log.info(json);
            String[] types = json.split("\\|");
            for (int i = 1; i < types.length; i++) {
                //split string into the name of the node, and it's sotred json
                String[] args = types[i].split("!");

                //get the node based on the stored name before the !, then suply the NodeSupplier with the json
                EventNode node = getNode(args[0]).get(args[1]);
                nodes.add(node);
                if (node.active) activeNodes.add(node);
            }
            active = true;
        } catch (Error e) {
            Log.err(e);
            Vars.state.rules.tags.put("er.sectorevents", "");
            active = false;
        }
    }

    public String writeNodes(){
        try{
            completeEntry = "";
            nodes.each(n -> {
                completeEntry = n.write(completeEntry);
            });
            Vars.state.rules.tags.put("er.sectorevents", completeEntry);
            nodes.clear();
            activeNodes.clear();
            Log.info(completeEntry);
            return completeEntry;
        }
        catch (Error e){
            Log.err(Varsr.username + ", you got an error: \n"+  Core.bundle.get("er.failsave"), e);
        }
        return "";
    }

    public void update(){
        if(!active) return;

        save = false;
        activeNodesClone = activeNodes.copy();
        activeNodesClone.each(n -> {
            n.update();

            //it's already saved, don't save nodes again
            if(!save && n.shouldSave()) {
                save = true;
            }
            if(!n.alwaysActive && n.finished()){
                for(int i = 0; i < n.activates.size; i++){
                    try {
                        EventNode act = nodes.get(n.activates.get(i));
                        activeNodes.add(act);
                        act.active = true;
                    }
                    catch (Error e){
                        n.activates.removeIndex(i);
                        Log.err("A Node was found trying to activate a node out of bounds.\nNode's position: + " + activeNodesClone.indexOf(n) + "\nError is below\n" + e);
                    }
                }

                n.active = false;
                activeNodes.remove(n);
            }
        });
        if(save) writeNodes();
    }

    public void draw(){

    }

    public interface NodeSupplier {
        EventNode get(String json);
    }

    //use if there's no extra nuance to reading and writing the node
    public static class EventNodeSupplier implements NodeSupplier {
        public Class nodeClass;

        public EventNodeSupplier(Class inputClass){
            nodeClass = inputClass;
        }

        @Override
        public EventNode get(String json) {
            return (EventNode) JsonIO.json.fromJson(nodeClass, json);
        }
    }

}
