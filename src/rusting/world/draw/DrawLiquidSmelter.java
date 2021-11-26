package rusting.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.production.GenericCrafter.*;
import mindustry.world.draw.DrawBlock;

public class DrawLiquidSmelter extends DrawBlock {
    public Color flameColor = Color.valueOf("ffc999");
    public TextureRegion liquid, top;
    public float flameRadius = 1.2f, flameRadiusIn = 0.25f, flameRadiusScl = 5f, flameRadiusMag = 2f, flameRadiusInMag = 1f;

    public DrawLiquidSmelter(){
    }

    public DrawLiquidSmelter(Color flameColor){
        this.flameColor = flameColor;
    }

    @Override
    public void load(Block block){
        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
    }

    @Override
    public void draw(GenericCrafterBuild entity){

        Draw.rect(entity.block.region, entity.x, entity.y, entity.block.rotate ? entity.rotdeg() : 0);

        if(entity.liquids.total() > 0.001f){
            Draw.color(entity.liquids().current().color);
            Draw.alpha(entity.liquids().currentAmount() / entity.block.liquidCapacity);
            Draw.rect(liquid, entity.x, entity.y, entity.block.rotate ? entity.rotdeg() : 0);
            Draw.color();
        }

        if(entity.warmup > 0f && flameColor.a > 0.001f){
            float g = 0.3f;
            float r = 0.06f;
            float cr = Mathf.random(0.1f);

            Draw.z(Layer.block + 0.01f);

            Draw.alpha(entity.warmup);
            Draw.rect(top, entity.x, entity.y);

            Draw.alpha(((1f - g) + Mathf.absin(Time.time, 8f, g) + Mathf.random(r) - r) * entity.warmup);

            Draw.tint(flameColor);
            Fill.circle(entity.x, entity.y, flameRadius + Mathf.absin(Time.time, flameRadiusScl, flameRadiusMag) + cr);
            Draw.color(1f, 1f, 1f, entity.warmup);
            Fill.circle(entity.x, entity.y, flameRadiusIn + Mathf.absin(Time.time, flameRadiusScl, flameRadiusInMag) + cr);

            Draw.color();
        }

        Draw.rect(top, entity.x, entity.y, entity.block.rotate ? entity.rotdeg() : 0);
    }
}
