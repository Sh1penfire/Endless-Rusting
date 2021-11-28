package rusting.world.blocks.pulse.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.*;
import mindustry.entities.Puddles;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import rusting.interfaces.*;
import rusting.interfaces.block.PulseCanalc;
import rusting.world.blocks.pulse.PulseBlock;
import rusting.world.blocks.pulse.utility.PulseTeleporterController.PulseTeleporterControllerBuild;

public class PulseCanal extends PulseBlock {
    public TextureRegion topRegion, liquidRegion, baseRegion, shineRegion, fullRegion;

    protected final static Queue<PulseCanalBuild> canalQueue = new Queue<>();

    public final int timerFlow = timers++;
    public float pulsePressure;

    public float reloadTime = 5;

    public PulseCanal(String name) {
        super(name);
        rotate = true;
        schematicPriority = 10;
        resistance = 0;
        connectable = false;
        liquidCapacity = 16f;
        liquidPressure = 1.025f;
        pulsePressure = 0.4f;
        pulseStorage = 40;
        hasLiquids = true;
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base", region);
        shineRegion = Core.atlas.find(name + "-shine", Core.atlas.find("empty"));
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find("empty"));
        liquidRegion = Core.atlas.find(name + "-liquid", Core.atlas.find("empty"));
        fullRegion = Core.atlas.find(name + "-full", region);
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.rect(baseRegion, req.drawx(), req.drawy(), req.rotation * 90);

        Draw.alpha(0.15f);
        Draw.rect(shineRegion, req.drawx(), req.drawy(), 270);
        Draw.alpha(1);
        Draw.z(Layer.blockOver + 0.1f);

        Draw.rect(topRegion, req.drawx(), req.drawy(), req.rotation * 90);
    }

    public static PulseCanalBuild asCanal(Building build){
        return (PulseCanalBuild) build;
    }

    private PulseTeleporterControllerBuild build;
    private static Building tmpBuild;
    private static Tile tmpTile = null;
    private static PulseCornerpiece corner = null;

    public class PulseCanalBuild extends PulseBlockBuild implements PulseCanalc {
        public float smoothLiquid = 0;
        public float reload = 0;

        public Seq<PulseInstantTransportation> connected = new Seq<PulseInstantTransportation>();

        public Tile canalEnding = tile;

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return source instanceof PulseCanalc && liquids.current() == liquid || liquids.currentAmount() < 0.2f
                    && canReceive(source);
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            canalEnding = next();
        }

        @Override
        public void updateTile(){
            if(canalEnding != null){
                if((liquids.total() > 0.001f || pulseModule.pulse >= movePulseAm()) && reload >= reloadTime){
                    //leak go br
                    customMoveLiquidForward(true, liquids.current());
                    reload = 0;
                }else{
                    reload += Time.delta;
                }
            }
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f);
        }

        public float movePulseAm(){return (pulseStorage + (canOverload ? overloadCapacity : 0)) * pulsePressure/60 * Time.delta;}

        public float customMoveLiquidForward(boolean leaks, Liquid liquid) {
            Building next = canalEnding.build;
            if (next != null) {
                if(pulseModule.pulse >= movePulseAm() && ((PulseCanalc) next).receivePulse(movePulseAm(), this)) removePulse(movePulseAm());
                return moveLiquid(next, liquid);
            } else if (!next.block().solid && !next.block().hasLiquids) {
                float leakAmount = liquids.get(liquid) / 1.5F;
                Puddles.deposit(next.tile, tile, liquid, leakAmount);
                liquids.remove(liquid, leakAmount);
            }
            return 0;
        }

        public Tile next(){
            Tile next = tile.nearby(rotation);
            if(next.build instanceof PulseCanalBuild && canConnect(next.build) && ((PulseCanalBuild) next.build).canReceive(this)){
                return next;
            }
            return null;
        }

        public boolean adjacentRotational(Tile t){
            return ((int) (t.build.angleTo(this)/90) + 1) % 2 == (rotation + 1) % 2;
        }

        @Override
        public boolean canConnect(Building b){
            return b instanceof PulseCanalc && (!b.block.rotate || b.rotation == rotation && adjacentRotational(b.tile));
        }

        public boolean canReceive(Building b){
            return canConnect(b);
        }

        @Override
        public boolean canReceivePulse(float pulse, Pulsec build) {
            return super.canReceivePulse(pulse, build) && (build instanceof PulseCanalc || build == this) && canReceive(((PulseBlockc) build).tile().build);
        }
        //Vars.world.buildWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y).setupEnding()

        @Override
        public void placed() {
            super.placed();
            canalEnding = next();
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y, rotation * 90);
            Drawf.liquid(liquidRegion, x, y, smoothLiquid, liquids.current().color, rotation * 90);
            Draw.color(chargeColourStart, chargeColourEnd, chargef());
            Draw.alpha(chargef());
            Draw.rect(chargeRegion, x, y, rotation * 90);
            Draw.color();
            Draw.alpha(0.15f);
            Draw.rect(shineRegion, x, y, 270);
            Draw.alpha(1);
            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y, rotation * 90);
        }
    }
}
