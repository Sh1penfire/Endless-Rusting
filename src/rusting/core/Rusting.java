package rusting.core;

import arc.Events;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.world.Tile;

//rusting system
public class Rusting extends Savable{

    private Tile tile;

    public Seq<rustModule> rust = new Seq<rustModule>();

    public class rustModule{
        int pos = 0;
        float rust = 0;
    }

    public void update(){

    }

    public void applyRust(int x, int y, float amount){
        applyRust(Point2.pack(x, y), amount);
    }

    public void applyRust(int pos, float amount){
            tile = Vars.world.tile(pos);
    }

    @Override
    public void write(Writes write) {
        super.write(write);
        Events.on(EventType.SaveLoadEvent.class, e -> {

        });
    }

    @Override
    public void read(Reads read, Byte version) {
        Vars.world.tiles.each((x, y) -> {
            rust.addAll(new rustModule(){{
                //it is assumed that the world is the same size every time, so resizing causes crashes.
                    tile = Vars.world.tileWorld(x, y);
                    rust = read.f();
                }}
            );

        });
    }
}
