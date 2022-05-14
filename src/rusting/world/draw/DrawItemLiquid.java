package rusting.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild;
import mindustry.world.draw.DrawBlock;

public class DrawItemLiquid extends DrawBlock {
    public TextureRegion liquid, top, bottom;

    @Override
    public void draw(Building entity){
        GenericCrafterBuild crafter = (GenericCrafterBuild) entity;
        float rotation = crafter.block.rotate ? crafter.rotdeg() : 0;

        Draw.rect(bottom, crafter.x, crafter.y, rotation);

        if(crafter.liquids.currentAmount() > 0.001f){
            Draw.color(crafter.liquids().current().color);
            Draw.alpha(crafter.liquids().currentAmount() / crafter.block.liquidCapacity);
            Draw.rect(liquid, crafter.x, crafter.y, rotation);
            Draw.color();
        }

        Draw.rect(top, crafter.x, crafter.y, rotation);
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
