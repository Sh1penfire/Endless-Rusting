package rusting.world.blocks.pulse.crafting;

import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Sounds;
import mindustry.type.*;
import mindustry.world.meta.*;
import rusting.world.blocks.pulse.PulseBlock;
import rusting.world.draw.DrawPulseBlock;

public class PulseGenericCrafter extends PulseBlock {
    public @Nullable
    ItemStack outputItem;
    public @Nullable
    LiquidStack outputLiquid;

    public float craftTime = 80;
    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public float updateEffectChance = 0.04f;
    public float warmupSpeed = 0.019f;

    public DrawPulseBlock drawer = new DrawPulseBlock();

    public PulseGenericCrafter(String name){
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
    }

    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }

    @Override
    public void init(){
       outputsLiquid = outputLiquid != null;
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);

        if(outputItem != null){
            stats.add(Stat.output, outputItem);
        }

        if(outputLiquid != null){
            stats.add(Stat.output, outputLiquid.liquid, outputLiquid.amount * (60f / craftTime), true);
        }
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.icons(this);
    }

    @Override
    public boolean outputsItems(){
        return outputItem != null;
    }

    public class PulseGenericCrafterBuild extends PulseBlockBuild{
        public float progress;
        public float totalProgress;
        public float warmup;

        @Override
        public void drawLight(){
            super.drawLight();
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public boolean shouldConsume(){
            if(outputItem != null && items.get(outputItem.item) + outputItem.amount > itemCapacity){
                return false;
            }
            return (outputLiquid == null || !(liquids.get(outputLiquid.liquid) >= liquidCapacity - 0.001f)) && enabled;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(consValid() && customConsumeValid()){

                progress += getProgressIncrease(craftTime);
                totalProgress += delta() * pulseEfficiency();
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);

                if(Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(getX() + Mathf.range(size * 4f), getY() + Mathf.range(size * 4));
                }
            }
            else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            if(progress >= 1f){
                consume();
                customConsume();

                if(outputItem != null){
                    for(int i = 0; i < outputItem.amount; i++){
                        offload(outputItem.item);
                    }
                }

                if(outputLiquid != null){
                    handleLiquid(this, outputLiquid.liquid, outputLiquid.amount);
                }

                craftEffect.at(x, y);
                progress %= 1f;
                craft();
            }

            if(outputItem != null && timer(timerDump, dumpTime / timeScale)){
                dump(outputItem.item);
            }

            if(outputLiquid != null){
                dumpLiquid(outputLiquid.liquid);
            }
        }

        @Override
        public int getMaximumAccepted(Item item){
            return itemCapacity;
        }

        @Override
        public boolean shouldAmbientSound(){
            return cons.valid() & customConsumeValid();
        }

        //called whenever an item is crafted, here for convenience sake
        public void craft(){

        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
        }
    }
}