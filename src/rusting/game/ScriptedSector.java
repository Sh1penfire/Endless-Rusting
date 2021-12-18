package rusting.game;

import arc.struct.Seq;
import mindustry.type.Planet;
import rusting.game.nodes.EventNode;

@Deprecated
public class ScriptedSector extends ERSectorPreset {

    //set after playing map, read only when necessary
    public final Seq<EventNode> nodes = new Seq();

    public ScriptedSector(String name, Planet planet, int sector) {
        super(name, planet, sector);
    }
}
