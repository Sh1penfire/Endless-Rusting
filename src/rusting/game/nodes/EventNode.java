package rusting.game.nodes;

import arc.struct.IntSeq;
import arc.struct.Queue;
import mindustry.io.JsonIO;
import rusting.game.actions.EventAction;

public class EventNode {

    //whether node is active, faster than using Varsr.sectors.activeNodes.contains(); If set to true, the node will be active from the start
    public boolean active = false;

    //whether node is always active
    public boolean alwaysActive = false;

    public Queue<EventAction> eventQueue = new Queue<EventAction>();

    //while node is active, update gets called
    public void update(){

    }

    //drawing is handled by the nodes, funny but it'll work out totaly fine
    public void draw(){

    }

    //if node is finished, remove it from activeNodes, and activate the nodes in activates
    public boolean finished(){
        return false;
    }

    //upon reading nodes, the scripted sector handler looks at the type, and creates a new node with that type. Stored in a function to avoid being written to json and from json
    public String type(){
        return "default";
    }

    //the positions of the nodes this node activates
    public IntSeq activates = IntSeq.with();

    //called when the game is being exited on client side, write the json and the name of the node here. No localization required for node names, as that'll break things
    public String write(String write){
        return write + "|" + type() + "!" + JsonIO.json.toJson(this, getClass());
    }

    //when returning true, save the nodes to Vars.state.rules.tags. Nodes are automaticaly saved upon being finished, so unless nescecary don't overide
    public boolean shouldSave(){
        return false;
    }
}