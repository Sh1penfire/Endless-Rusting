package rusting.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.type.Liquid;
import mindustry.world.Block;
import rusting.world.blocks.pulse.crafting.PulseGenericCrafter.PulseGenericCrafterBuild;

public class DrawPulseLiquidCrafter extends DrawPulseBlock{
    private Liquid currentLiquid = null;
    public TextureRegion liquid, top, bottom, rotator;

    @Override
    public void draw(PulseGenericCrafterBuild build){
        float rotation = build.block.rotate ? build.rotdeg() : 0;

        Draw.rect(bottom, build.x, build.y, rotation);

        if(build.liquids.currentAmount() > 0.001f){
            currentLiquid = build.liquids.current();
            Draw.color(currentLiquid.color);
            Draw.alpha(build.liquids.get(currentLiquid) / build.block.liquidCapacity);
            Draw.rect(liquid, build.x, build.y, rotation);
            Draw.color();
        }

        Draw.rect(rotator, build.x, build.y, build.totalProgress * 2);
        Draw.rect(top, build.x, build.y, rotation);
    }

    @Override
    public void load(Block block){
        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
        bottom = Core.atlas.find(block.name + "-bottom");
        rotator = Core.atlas.find(block.name + "-rotator");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{bottom, rotator, top};
    }
}
