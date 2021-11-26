package rusting.world.blocks;

import arc.graphics.Color;
import mindustry.graphics.MultiPacker;
import mindustry.world.blocks.environment.StaticWall;

public class OverrideColourStaticWall extends StaticWall {
    public Color overrideMapColour = mapColor;

    public OverrideColourStaticWall(String name) {
        super(name);
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);
        mapColor.set(overrideMapColour);
    }
}
