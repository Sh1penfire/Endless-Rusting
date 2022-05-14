package rusting.world.blocks.environment;

import mindustry.world.blocks.environment.Floor;

public class IdTakingFloorBlock extends Floor {

    public IdTakingFloorBlock(String name) {
        super(name);
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
