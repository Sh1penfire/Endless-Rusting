package rusting.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild;
import mindustry.world.draw.DrawBlock;

public class DrawItemLiquid extends DrawBlock {
    public TextureRegion liquid, top, bottom;

    @Override
    public void draw(GenericCrafterBuild entity){
        float rotation = entity.block.rotate ? entity.rotdeg() : 0;

        Draw.rect(bottom, entity.x, entity.y, rotation);

        if(entity.liquids.total() > 0.001f){
            Draw.color(entity.liquids().current().color);
            Draw.alpha(entity.liquids().currentAmount() / entity.block.liquidCapacity);
            Draw.rect(liquid, entity.x, entity.y, rotation);
            Draw.color();
        }

        Draw.rect(top, entity.x, entity.y, rotation);
    }

    @Override
    public void load(Block block){
        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
        bottom = Core.atlas.find(block.name + "-bottom");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{bottom, top};
    }
}
