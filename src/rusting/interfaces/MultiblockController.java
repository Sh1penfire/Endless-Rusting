package rusting.interfaces;

import arc.struct.Seq;
import mindustry.world.Tile;
import rusting.world.modules.MultiblockPart;

public interface MultiblockController {
    Seq<MultiblockPart> blocks = new Seq<>();

    //used to check for if the multiblock is formed
    default boolean multiblockFormed(MultiblockController block, Tile tile){
        return false;
    };
}
