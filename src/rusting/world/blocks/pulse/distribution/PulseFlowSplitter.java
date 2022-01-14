package rusting.world.blocks.pulse.distribution;

import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import rusting.interfaces.Pulsec;
import rusting.interfaces.block.PulseCanalc;

//acts as a router for Pulse Canals, and as an output terminal for them.
public class PulseFlowSplitter extends PulseCanal {

    protected static Seq<Pulsec> distribute = Seq.with();

    public PulseFlowSplitter(String name) {
        super(name);
        rotate = false;
        schematicPriority = 10;
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.rect(baseRegion[0], req.drawx(), req.drawy(), 0);

        Draw.alpha(0.15f);
        Draw.rect(shineRegion[0], req.drawx(), req.drawy(), 0);
        Draw.alpha(1);
        Draw.z(Layer.blockOver + 0.1f);

        Draw.rect(topRegion[0], req.drawx(), req.drawy(), 0);
    }

    public class PulseFlowSplitterBuild extends PulseCanalBuild{

        @Override
        public void draw() {
            Draw.rect(baseRegion[0], x, y, 0);
            Draw.color(chargeColourStart, chargeColourEnd, chargef());
            Draw.alpha(chargef());
            Draw.rect(pulseRegion, x, y, 0);
            Draw.color();
            Draw.alpha(0.15f);
            Draw.rect(shineRegion[0], x, y, 0);
            Draw.alpha(1);
            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion[0], x, y, 0);
        }

        @Override
        public void updateTile(){
            if(pulseModule.pulse >= moveAmount() && reload >= reloadTime){
                movePulse();
                reload %= reloadTime;
            }else{
                reload += Time.delta;
            }
        }

        @Override
        public void movePulse() {
            for (int i = 0; i < 4; i++) {
                Tile next = Vars.world.tileWorld(x + Tmp.v1.trns(i * 90, 8).x, y + Tmp.v1.y);
                if(next.build instanceof Pulsec && ((Pulsec) next.build).canReceivePulse(moveAmount(), this)){
                    distribute.add((Pulsec) next.build);
                }
            }
            distribute.each(build -> {
                if(build.receivePulse(moveAmount()/distribute.size, this)) removePulse(moveAmount()/ distribute.size);
            });
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
