package rusting.game;

import arc.struct.IntMap;

/**
 A controller for a sector. Map using {@link SectorController#mapSector} a short for {@link ScriptedSectorHandler} to use.
 Instantiate as a static field
 */
public abstract class SectorController {
    public static IntMap<SectorController> idMap = IntMap.of();
    public int id;
    private boolean mapped;

    public boolean mapped(){
        return mapped;
    }

    public static void mapSector(SectorController controller, int id){
        idMap.put(id, controller);
        controller.asignId(id);
    }

    //Called after mapping the controller
    public void asignId(int id){
        this.id = id;
        mapped = true;
    }

    //Triggers when the game is not paused, and is playing
    public abstract void update();
    //Triggers when the game is playing
    public abstract void draw();
    //Triggered when a wave is sent
    public abstract void wave();

    //Method used to save data to tags. You have to call this manually
    public abstract void save();
    //Method used to load data from tags. Called whenever the controller is attached to the handler.
    public abstract void load();
}
