package rusting.world.blocks.pulse.utility;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.*;
import mindustry.Vars;
import mindustry.entities.Puddles;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import rusting.content.Fxr;
import rusting.interfaces.block.PulseCanalc;
import rusting.interfaces.Pulsec;
import rusting.world.blocks.pulse.distribution.PulseCanal;

//acts as a router for Pulse Canals, and as an output terminal for them.
public class PulseFlowSplitter extends PulseCanal {

    public PulseFlowSplitter(String name) {
        super(name);
        rotate = false;
        schematicPriority = 10;
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.rect(baseRegion, req.drawx(), req.drawy(), 0);

        Draw.alpha(0.15f);
        Draw.rect(shineRegion, req.drawx(), req.drawy(), 0);
        Draw.alpha(1);
        Draw.z(Layer.blockOver + 0.1f);

        Draw.rect(topRegion, req.drawx(), req.drawy(), 0);
    }

    public class PulseFlowSplitterBuild extends PulseCanalBuild{

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y, 0);
            Drawf.liquid(liquidRegion, x, y, smoothLiquid, liquids.current().color, rotation * 90);
            Draw.color(chargeColourStart, chargeColourEnd, chargef());
            Draw.alpha(chargef());
            Draw.rect(chargeRegion, x, y, 0);
            Draw.color();
            Draw.alpha(0.15f);
            Draw.rect(shineRegion, x, y, 0);
            Draw.alpha(1);
            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y, 0);
        }

        @Override
        public void updateTile(){
            if((liquids.total() > 0.001f || pulseModule.pulse >= movePulseAm()) && reload >= reloadTime){
                //leak go br
                customMoveLiquidForward(true, liquids.current());
                reload = 0;
            }else{
                reload += Time.delta;
            }
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f);
        }

        @Override
        public float customMoveLiquidForward(boolean leaks, Liquid liquid) {
            for (int i = 0; i < 4; i++) {
                Tile next = Vars.world.tileWorld(x + Tmp.v1.trns(i * 90, 8).x, y + Tmp.v1.y);
                Fxr.healingWaterSmoke.at(next.worldx(), next.worldy());
                if(next.build instanceof PulseCanalc) {
                    if(((PulseCanalc) next.build).canReceive(this)){
                        if(next.build.acceptLiquid(this, liquid)) moveLiquid(next.build, liquid);
                        if(pulseModule.pulse >= movePulseAm() && ((PulseCanalc) next.build).receivePulse(movePulseAm(), this)) removePulse(movePulseAm());
                    }
                }
                else if (next.build != null && next.build.block.hasLiquids){
                    moveLiquid(next.build, liquid);
                }
                else if (!next.block().solid || !next.block().hasLiquids){
                    //float precision lmao
                    float leakAmount = liquids.get(liquid) / 1.5F / 4F;
                    Puddles.deposit(next, tile, liquid, leakAmount);
                    liquids.remove(liquid, leakAmount);
                }
            }
            return 0;
        }

        @Override
        public boolean canConnect(Building b) {
            return b instanceof PulseCanalc;
        }

        @Override
        public boolean canReceive(Building b) {
            return b instanceof PulseCanalc;
        }

        @Override
        public boolean canReceivePulse(float pulse, Pulsec build){
            return build instanceof PulseCanalc && pulse + pulseModule.pulse < pulseStorage + (canOverload ? overloadCapacity : 0);
        }
    }
}
