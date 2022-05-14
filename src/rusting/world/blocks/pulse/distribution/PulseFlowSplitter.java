package rusting.world.blocks.pulse.distribution;

import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import rusting.graphics.Drawr;
import rusting.interfaces.Pulsec;
import rusting.interfaces.block.PulseCanalc;

//acts as a router for Pulse Canals, and as an output terminal for them.
public class PulseFlowSplitter extends PulseCanal {

    protected static Seq<Pulsec> distribute = Seq.with();

    public PulseFlowSplitter(String name) {
        super(name);
        rotate = false;
        schematicPriority = 10;
        pulseCapacity = 45;
        pulsePressure = 25;
    }

    @Override
    public void drawPlanRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.rect(baseRegion[0], req.drawx(), req.drawy(), 0);

        Drawr.drawShine(shineRegion[0], req.drawx(), req.drawy(),0, 0.25f);
        Draw.alpha(1);
        Draw.z(Layer.blockOver + 0.1f);

        Draw.rect(topRegion[0], req.drawx(), req.drawy(), 0);
    }

    public class PulseFlowSplitterBuild extends PulseCanalBuild{

        public Seq<Pulsec> distributeTo = Seq.with();

        @Override
        public void setupCanal(){
            findTiles();
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion[0], x, y, 0);
            Draw.color(chargeColourStart, chargeColourEnd, chargef());
            Draw.alpha(chargef());
            Draw.rect(pulseRegion, x, y, 0);
            Draw.color();
            Drawr.drawShine(shineRegion[0], x, y, 0, 0.25f);
            Draw.alpha(1);
            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion[0], x, y, 0);
        }

        public void findTiles(){

            distributeTo.clear();

            for (int i = 0; i < 4; i++) {
                Tile next = Vars.world.tileWorld(x + Tmp.v1.trns(i * 90, 8).x, y + Tmp.v1.y);
                if(next.build instanceof Pulsec && !(next.build instanceof PulseCanalc && !((PulseCanalc) next.build).canReceive(this))){
                    distributeTo.add((Pulsec) next.build);
                }
            }
        }

        @Override
        public void updateTile(){
            if(storage.pulse >= moveAmount() && reload >= reloadTime){
                movePulse();
                reload %= reloadTime;
            }else{
                reload += Time.delta;
            }
        }

        @Override
        public void movePulse() {
            distribute.clear();

            distributeTo.each(b -> {
                if(!((Entityc) b).isAdded()) {
                    distribute.add(b);
                    return;
                }
                if(b.canReceivePulse(moveAmount()/distributeTo.size, this)) removePulse(b.addPulse(moveAmount()/distributeTo.size));
            });

            distribute.each(b -> distributeTo.remove(b));
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
            return build instanceof PulseCanalc && pulse + storage.pulse < pulseCapacity + (canOverload ? overloadCapacity : 0);
        }
    }
}
