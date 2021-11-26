package rusting.world;

import arc.Events;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.world.Tile;
import rusting.world.blocks.environment.UpdateFloor;

public class Worldr {

    public Worldr(){

        Events.on(Trigger.update.getClass(), e -> {
            if(Vars.state.isPaused() || !Vars.state.isPlaying() || Vars.state.isEditor()) return;
            udpateTiles.each(t -> {
                if(t.floor() instanceof UpdateFloor) ((UpdateFloor) t.floor()).update(t);
                else udpateTiles.remove(t);
            });
        });
        
        Events.on(EventType.WorldLoadEvent.class, e -> {
            udpateTiles.clear();
            Vars.world.tiles.eachTile(t -> {
                if(t.floor() instanceof UpdateFloor) udpateTiles.add(t);
            });
        });
    }

    public Seq<Tile> udpateTiles = Seq.with();
    private boolean returnBool = false;

    public boolean onTile(Building building, Seq<Integer> list){
        returnBool = false;
        list.each(i -> {
            if(returnBool) return;
            if(building == Vars.world.build(i)) returnBool = true;
        });
        return returnBool;
    }

}
