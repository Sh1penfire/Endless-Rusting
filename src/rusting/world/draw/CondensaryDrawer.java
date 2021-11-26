package rusting.world.draw;

import arc.Core;
import arc.graphics.g2d.*;
import arc.util.Tmp;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import rusting.content.Palr;
import rusting.graphics.RustedShaders;
import rusting.world.blocks.pulse.crafting.PulseGenericCrafter.PulseGenericCrafterBuild;

public class CondensaryDrawer extends DrawPulseBlock{
    public TextureRegion liquid, top;
    private float fout, fin;

    @Override
    public void draw(PulseGenericCrafterBuild entity) {
        super.draw(entity);
        if(entity.liquids.total() > 0.001f){
            Draw.color(entity.liquids.current().color);
            Draw.alpha(entity.liquids.total() / entity.block.liquidCapacity);
            Draw.rect(liquid, entity.x, entity.y, 0);
            Draw.color();
        }

        Draw.z(Layer.blockOver);
        Draw.color(Palr.pulseChargeStart, Palr.darkerPulseChargeStart, Palr.pulseBullet, entity.progress % 1);
        Draw.alpha(entity.warmup);
        Draw.rect(top, entity.x, entity.y, 0);

        Draw.draw(Layer.effect, () -> {
            fin = (entity.progress % 1);
            fout = 1 - (entity.progress % 1);
            Draw.shader(RustedShaders.sharpeffect);
            boolean effects = Core.settings.getBool("settings.er.advancedeffects");
            boolean bloom = Core.settings.getBool("bloom");
            float radius = effects ? 125 : 15;
            float smallerRadius = effects ? 46 : 7;
            float alpha = bloom ? fin * entity.warmup : fin * fin;
            Draw.alpha(entity.warmup * fin);
            Fill.circle(entity.x, entity.y, fout * 8);
            Fill.light(entity.x, entity.y, 16, radius, Tmp.c1.set(Palr.pulseBullet).a(alpha * entity.warmup), Tmp.c2.set(effects ? Palr.darkerPulseChargeStart : Palr.pulseChargeEnd).a((1 - alpha) * 0.85f * entity.warmup));
            Fill.light(entity.x, entity.y, 8, smallerRadius, Tmp.c3.set(Pal.lightTrail).a(alpha * 0.75f * entity.warmup), Tmp.c4.set(Palr.pulseBullet).a((1 - alpha) * 0.65f * entity.warmup));
            Draw.shader();
            Draw.reset();
        });
    }


    @Override
    public void load(Block block){
        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
    }
}
