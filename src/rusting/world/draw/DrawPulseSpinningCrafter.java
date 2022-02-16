package rusting.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.world.Block;
import rusting.world.blocks.pulse.PulseBlock;
import rusting.world.blocks.pulse.crafting.PulseGenericCrafter.PulseGenericCrafterBuild;

public class DrawPulseSpinningCrafter extends DrawPulseBlock {
    public TextureRegion pulse, top, bottom, rotator;

    @Override
    public void draw(PulseGenericCrafterBuild build){
        float rotation = build.block.rotate ? build.rotdeg() : 0;

        Draw.rect(bottom, build.x, build.y, rotation);

        Draw.color(((PulseBlock) build.block).chargeColourStart);
        Draw.alpha(build.chargef());
        Draw.rect(pulse, build.x, build.y, rotation);
        Draw.alpha(1);
        Draw.color();

        Draw.rect(rotator, build.x, build.y, build.totalProgress * 2);
        Draw.rect(top, build.x, build.y, rotation);
    }

    @Override
    public void load(Block block){
        pulse = Core.atlas.find(block.name + "-pulse");
        top = Core.atlas.find(block.name + "-top");
        bottom = Core.atlas.find(block.name + "-bottom");
        rotator = Core.atlas.find(block.name + "-rotator");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{bottom, rotator, top};
    }
}
