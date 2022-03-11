package rusting.world.blocks.pulse.distribution;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Queue;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import rusting.interfaces.*;
import rusting.interfaces.block.PulseCanalc;

public class PulseCanalInput extends PulseCanal {

    //Reload of the node till it can transmit a pulse to a nearby block
    public float pulseReloadTime = 10;
    //How many bursts the node sends
    public float pulseBursts = 1;
    //Spacing between bursts
    public float pulseBurstSpacing = 0;
    //How much energy is transmitted
    public float energyTransmission = 3;

    private Building tmpBuilding;

    protected final static Queue<PulseCanalBuild> canalQueue = new Queue<>();

    public PulseCanalInput(String name) {
        super(name);
        connectable = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.reload, pulseReloadTime /60);
    }

    @Override
    public void setPulseStats() {
        super.setPulseStats();
        pStats.pulseReloadTime.setValue(60/pulseReloadTime);
        pStats.energyTransmission.setValue(energyTransmission);
        pStats.pulseBursts.setValue(pulseBursts);
        pStats.pulseBurstSpacing.setValue(pulseBurstSpacing);
    }


    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.rect(baseRegion[0], req.drawx(), req.drawy(), req.rotation * 90);

        Draw.alpha(0.15f);
        Draw.rect(shineRegion[0], req.drawx(), req.drawy(), 270);
        Draw.alpha(1);
        Draw.z(Layer.blockOver + 0.1f);

        Draw.rect(topRegion[0], req.drawx(), req.drawy(), req.rotation * 90);
    }

    public class PulseCanalInputBuild extends PulseCanalBuild implements rusting.interfaces.PulseCanalInput, PrimitiveControlBlock {
        public float reload = 0;

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquids.current() == liquid || liquids.currentAmount() < 0.2f;
        }

        public boolean adjacentRotational(Tile t){
            return (int) (t.build.angleTo(this) + 2) % 3 == rotation;
        }

        @Override
        public boolean canConnect(Building b) {
            return b instanceof PulseCanalc && (!b.block.rotate || b.rotation == this.rotation);
        }

        @Override
        public boolean canReceive(Building b) {
            return false;
        }

        @Override
        public boolean canReceivePulse(float pulse, Pulsec build){
            return pulse + storage.pulse < pulseCapacity + (canOverload ? overloadCapacity : 0);
        }

        @Override
        public void rawControl(double p1, double p2, double p3, double p4){
            rotation = ((int) p2)/90 + 1;
            enabled = !Mathf.zero(p3);
        }

        @Override
        public void exportInformationDefault(Building build){
            if(build instanceof PrimitiveControlBlock) ((PrimitiveControlBlock) build).rawControl(1, rotation * 90 - 90, enabled ? 1 : 0, 1F);
        }
    }
}
