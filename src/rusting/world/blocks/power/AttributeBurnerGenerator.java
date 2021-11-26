package rusting.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.power.BurnerGenerator;
import mindustry.world.consumers.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class AttributeBurnerGenerator extends BurnerGenerator {

    public float itemEfficyMultiplier = 1.25f;
    public float turbineMaxSpeed = 2.25f;
    public Effect generateEffect = Fx.none;
    public Attribute attribute = Attribute.heat;

    public AttributeBurnerGenerator(String name) {
        super(name);
        ambientSound = Sounds.hum;
        ambientSoundVolume = 0.06f;
        //items get used really quickly
        itemDuration = 250;
        setDefaults();
    }

    @Override
    protected void setDefaults(){
        if(hasItems){
            consumes.add(new ConsumeItemFilter(item -> getItemEfficiency(item) >= minItemEfficiency)).update(false).optional(true, false);
        }

        defaults = true;
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

    public class AttributeBurnerGeneratorBuild extends BurnerGeneratorBuild {
        public float baseSum, sum, turbineSpeedMultiplier = 1, turbineRotation = 0;
        public boolean itemAvailable = false;

        @Override
        public void updateTile() {

            float calculationDelta = delta();

            Liquid liquid = null;

            for(Liquid other : content.liquids()){
                if(hasLiquids && liquids.get(other) >= 0.001f){
                    liquid = other;
                    break;
                }
            }

            //it is assumed that the block is on a valid floor, therefore, the liquid can be used

            if(hasLiquids && liquid != null){

                float maximumPossible = maxLiquidGenerate * calculationDelta;

                ConsumeLiquid consumer = null;

                if(consumes.get(ConsumeType.liquid) instanceof ConsumeLiquid) consumer = (ConsumeLiquid) consumes.get(ConsumeType.liquid);

                if(consumer != null) {
                    liquids.remove(liquid, consumer.amount);
                    heat = Mathf.lerpDelta(heat, 1f, 0.05f);
                }
            }
            else heat = Mathf.lerpDelta(heat, generateTime >= 0.001f && enabled ? 1f : 0f, 0.025f);

            totalTime += heat * Time.delta;

            turbineRotation += heat * Time.delta * turbineSpeedMultiplier;

            if(!cons.valid()) {
                productionEfficiency = 0;
                return;
            }

            if (hasItems) {
                // No liquids accepted or none supplied, try using items if accepted
                if (items.total() > 0) {
                    if(generateTime <= 0f) {
                        generateEffect.at(x + Mathf.range(3f), y + Mathf.range(3f));
                        Item item = items.take();
                        itemAvailable = true;
                        sum = getItemEfficiency(item) * itemEfficyMultiplier+ baseSum;
                        explosiveness = item.explosiveness;
                        generateTime = 1f;
                    }
                }
                else {
                    sum = baseSum;
                    itemAvailable = false;
                }

                if (generateTime > 0f) {
                    generateTime -= Math.min(1f / itemDuration * delta() * power.graph.getUsageFraction(), generateTime);

                    if (randomlyExplode && state.rules.reactorExplosions && Mathf.chance(delta() * 0.06 * Mathf.clamp(explosiveness - 0.5f))) {
                        //this block is run last so that in the event of a block destruction, no code relies on the block type
                        Core.app.post(() -> {
                            damage(Mathf.random(11f));
                            explodeEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                        });
                    }
                }
            }

            turbineSpeedMultiplier = Mathf.lerpDelta(turbineSpeedMultiplier, generateTime >= 0.001f && itemAvailable ? turbineMaxSpeed : 1f, 0.025f);

            productionEfficiency = sum + attribute.env();
            if (cons.optionalValid()) {
                if (liquids().current() != null) {
                    productionEfficiency += getLiquidEfficiency(liquids().current());
                }
            }

            if (productionEfficiency > 0.1 && Mathf.chance(0.05 * delta())) {
                generateEffect.at(x + Mathf.range(3), y + Mathf.range(3));
            }
        }

        @Override
        public void drawLight(){
            Drawf.light(team, x, y, (40f + Mathf.absin(10f, 5f)) * Math.min(productionEfficiency, 2f) * size, Color.scarlet, 0.4f);
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            baseSum = sumAttribute(attribute, tile.x, tile.y);
        }

        @Override
        public void draw() {
            Draw.rect(block.region, x, y, block.rotate ? rotdeg() : 0);
            drawTeamTop();

            if(hasItems){
                Draw.color(heatColor);
                Draw.alpha(heat * 0.4f + Mathf.absin(Time.time, 8f, 0.6f) * heat);
                Draw.rect(topRegion, x, y);
                Draw.reset();
            }

            if(hasLiquids){
                Drawf.liquid(liquidRegion, x, y, liquids.total() / liquidCapacity, liquids.current().color);
            }

            if(turbineRegions[0].found()){
                Draw.rect(turbineRegions[0], x, y, turbineRotation);
                Draw.rect(turbineRegions[1], x, y, -turbineRotation);

                Draw.rect(capRegion, x, y);

                if(hasLiquids){
                    Drawf.liquid(liquidRegion, x, y, liquids.total() / liquidCapacity, liquids.current().color);
                }
            }
        }
    }
}
