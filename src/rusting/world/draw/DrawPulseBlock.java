package rusting.world.draw;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.world.Block;
import rusting.world.blocks.pulse.crafting.PulseGenericCrafter.PulseGenericCrafterBuild;

public class DrawPulseBlock {

    /** Draws the block. */
    public void draw(PulseGenericCrafterBuild entity){
        Draw.rect(entity.block.region, entity.x, entity.y, entity.block.rotate ? entity.rotdeg() : 0);

    }

    /** Load any relevant texture regions. */
    public void load(Block block){

    }

    /** @return the generated icons to be used for this block. */
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{block.region};
    }
}
