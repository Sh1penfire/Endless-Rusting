package rusting.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.type.*;
import mindustry.world.Tile;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.consumers.ConsumeItemExplode;
import mindustry.world.consumers.ConsumeItemFlammable;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;

//general purpose class to be used for power generators with atribuutes and item outputs
public class AttributeBurnerGenerator extends ConsumeGenerator {

    //uses a timer for crafting instead of producing items on consume
    public boolean separateCraftTimer = false;
    public float itemEfficyMultiplier = 1.25f;
    public float turbineMaxSpeed = 2.25f;
    public Effect generateEffect = Fx.none;
    public Attribute attribute = Attribute.heat;
    public ItemStack[] outputItems;
    public LiquidStack outputLiquid;

    public AttributeBurnerGenerator(String name) {
        super(name);
        ambientSound = Sounds.hum;
        ambientSoundVolume = 0.06f;
        //items get used really quickly
        itemDuration = 250;
        setDefaults();
    }

    protected void setDefaults(){
        consume(new ConsumeItemFlammable());
        consume(new ConsumeItemExplode());
    }

    @Override
    public void init(){
        super.init();
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.tiles, attribute, floating);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        drawPlaceText(Core.bundle.formatFloat("bar.efficiency", sumAttribute(attribute, x, y) * 100, 1), x, y, valid);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team){
        //make sure there's heat at this location
        return tile.getLinkedTilesAs(this, tempTiles).sumf(other -> other.floor().attributes.get(attribute)) > 0.01f;
    }

    public class AttributeBurnerGeneratorBuild extends ConsumeGeneratorBuild {
        public float baseSum, sum, turbineSpeedMultiplier = 1, turbineRotation = 0;
        public boolean itemAvailable = false;


        @Override
        public boolean shouldConsume(){
            if(outputItems != null){
                for(ItemStack output : outputItems){
                    if(items.get(output.item) + output.amount > itemCapacity){
                        return false;
                    }
                }
            }
            return (outputLiquid == null || !(liquids.get(outputLiquid.liquid) >= liquidCapacity - 0.001f)) && enabled;
        }

        @Override
        public void updateTile() {
            boolean valid = this.efficiency > 0.0F;
            this.warmup = Mathf.lerpDelta(this.warmup, valid ? 1.0F : 0.0F, 0.05F);
            this.productionEfficiency = this.efficiency * this.efficiencyMultiplier + baseSum;
            this.totalTime += this.warmup * Time.delta;

            if (valid && Mathf.chanceDelta(effectChance)) {
                generateEffect.at(this.x + Mathf.range(generateEffectRange), this.y + Mathf.range(generateEffectRange));
            }

            if (hasItems && valid && this.generateTime <= 0.0F) {
                this.consume();
                consumeEffect.at(this.x + Mathf.range(generateEffectRange), this.y + Mathf.range(generateEffectRange));
                this.generateTime = 1.0F;
                craft();
            }

            dumpOutputs();

            if (liquidOutput != null) {
                float added = Math.min(this.productionEfficiency * this.delta() * liquidOutput.amount, liquidCapacity - this.liquids.get(liquidOutput.liquid));
                this.liquids.add(liquidOutput.liquid, added);
                this.dumpLiquid(liquidOutput.liquid);
            }

            this.generateTime -= this.delta() / itemDuration;
        }


    public void craft(){
        consume();

        if(outputItems != null){
            for(ItemStack output : outputItems){
                for(int i = 0; i < output.amount; i++){
                    offload(output.item);
                }
            }
        }

        if(outputLiquid != null){
            handleLiquid(this, outputLiquid.liquid, outputLiquid.amount);
        }
    }

    public void dumpOutputs(){
        if(outputItems != null && timer(timerDump, dumpTime / timeScale)){
            for(ItemStack output : outputItems){
                dump(output.item);
            }
        }

        if(outputLiquid != null){
            dumpLiquid(outputLiquid.liquid);
        }
    }
        @Override
        public void drawLight(){
            Drawf.light(x, y, (40f + Mathf.absin(10f, 5f)) * Math.min(productionEfficiency, 2f) * size, Color.scarlet, 0.4f);
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            baseSum = sumAttribute(attribute, tile.x, tile.y);
        }
    }
}
