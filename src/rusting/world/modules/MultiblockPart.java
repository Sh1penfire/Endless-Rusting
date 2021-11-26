package rusting.world.modules;

import arc.math.geom.Vec2;
import mindustry.content.Blocks;
import mindustry.world.Block;

//used for single blocks in specific locations.
public class MultiblockPart {

    public MultiblockPart(Block block){
        this(block, new Vec2());
    }

    public MultiblockPart(Vec2 pos){
        this(Blocks.copperWall, pos);
    }

    public MultiblockPart(Block block, Vec2 relativePosition){
        this(block, relativePosition, false);
    }

    public MultiblockPart(Block block, int x, int y){
        this(block, x, y, false);
    }

    public MultiblockPart(Block block, Vec2 relativePosition, boolean optional){
        this.block = block;
        this.relativePosition.set(relativePosition);
    }

    public MultiblockPart(Block block, int x, int y, boolean optional){
        this.block = block;
        this.relativePosition.set(x, y);
    }

    //block component
    Block block = Blocks.copperWall;
    //in world units
    Vec2 relativePosition = new Vec2(1, 1);
    //if the part is optional in the build
    public boolean optional;
}
