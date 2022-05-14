package rusting.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild;
import mindustry.world.draw.DrawBlock;

public class DrawRotorTop extends DrawBlock {
    public TextureRegion rotator, top;
    @Override
    public void draw(Building entity){
        GenericCrafterBuild crafter = (GenericCrafterBuild) entity;
        
        Draw.rect(crafter.block.region, crafter.x, crafter.y);
        Draw.rect(rotator, crafter.x, crafter.y, crafter.totalProgress * 2f);
        Draw.z(Layer.blockOver);
        Draw.rect(top, crafter.x, crafter.y);
    }

    @Override
    public void load(Block block){
        rotator = Core.atlas.find(block.name + "-rotator");
        top = Core.atlas.find(block.name + "-top");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{block.region, rotator, top};
    }
}
