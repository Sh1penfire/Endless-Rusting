package rusting.world.blocks.environment;

import arc.graphics.Color;
import arc.util.Nullable;
import mindustry.graphics.MultiPacker;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OreBlock;

//it isn't fixed yet unfortunately
public class FixedOreBlock extends OreBlock {

    @Nullable
    public Color overrideMapColor = null;

    public FixedOreBlock(Item ore) {
        super(ore);
        useColor = true;
    }

    @Override
    public void init() {
        super.init();
        useColor = true;
    }

    public FixedOreBlock(String name) {
        super(name);
    }

    @Override
    public int minimapColor(Tile tile) {
        return overrideMapColor.rgba();
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);
        mapColor.set(overrideMapColor != null ? overrideMapColor : itemDrop.color);
    }
}
