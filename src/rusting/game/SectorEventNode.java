package rusting.game;

public class SectorEventNode {
    Runnable r = null;


    //whether the event is running right now
    public boolean running = false;

    //whether the node has been activated already
    public boolean run = false;

    public boolean valid(){
        return false;
    }
}