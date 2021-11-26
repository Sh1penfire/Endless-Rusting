package rusting.world.blocks.pulse.production;

import arc.util.Time;
import mindustry.world.meta.Stat;
import rusting.interfaces.Pulsec;
import rusting.world.blocks.pulse.distribution.PulseNode;

public class PulseGenerator extends PulseNode {

    public float pulseAmount = 1;
    public float productionTime = 30;

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.basePowerGeneration, pulseAmount/productionTime * 60);
    }

    @Override
    public void setPulseStats() {
        super.setPulseStats();
        pStats.pulseProduced.setValue(pulseAmount);
        pStats.pulseProductionInterval.setValue(productionTime/60);
    }

    public PulseGenerator(String name) {
        super(name);
        resistance = 0;
    }

    @Override
    public boolean outputsItems() {
        return false;
    }

    public class PulseGeneratorBuild extends PulseNodeBuild{
        public float currentProductionTime = 0;

        @Override
        public void updateTile() {
            super.updateTile();
            if(currentProductionTime >= productionTime && allConsValid() && canReceivePulse(pulseAmount, this)) {
                consume();
                customConsume();
                producePulse();
                currentProductionTime = 0;
            }
            else currentProductionTime += pulseEfficiency() * Time.delta;
        }

        //something can happen when the generator makes a pulse, just override the function instead
        public void producePulse(){
            receivePulse(pulseAmount, (Pulsec) this);
        }
    }
}
