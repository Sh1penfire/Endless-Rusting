package rusting.world;

import arc.Events;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.world.Tile;
import rusting.world.blocks.environment.UpdateFloor;
import rusting.world.blocks.pulse.PulseBoulder.PulseBoulderBuild;

public class Worldr {

    public Seq<Tile> udpateTiles = Seq.with();
    //make the use of Vars.indexer unescecary
    public Seq<Tile> geodeTiles = Seq.with();
    public Seq<PulseBoulderBuild> geodeBuildings = Seq.with();

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
                if(t.build instanceof PulseBoulderBuild) {
                    //if it already contains the build's tile it must have already added the build aswel
                    if(!geodeTiles.contains(t.build.tile)) {
                        geodeTiles.add(t.build.tile);
                        geodeBuildings.add((PulseBoulderBuild) t.build);
                    }
                }
            });
        });
    }

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
