package rusting.world.blocks.pulse.distribution;

import arc.util.Time;
import mindustry.world.meta.Stat;
import rusting.interfaces.Pulsec;
import rusting.world.blocks.pulse.PulseBlock;

//preferably used for batteries since syphons are unlocked later
public class ConductivePulseBlock extends PulseBlock {
    //Reload of the node till it can transmit a pulse to a nearby block. Preferably not every tick
    public float reloadTime = 5;
    //How many bursts the plating sends
    public float bursts = 1;
    //Spacing between bursts
    public float burstSpacing = 0;
    //How much energy is transmitted
    public float energyTransmission = 1;

    public ConductivePulseBlock(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.reload, reloadTime * 60);
    }

    public class ConductivePulseBlockBuild extends PulseBlock.PulseBlockBuild {
        public float reload = 0;

        @Override
        public void updateTile(){
            super.updateTile();
            if(reload >= reloadTime){
                reload = 0;
                addPulseAdjacent();
            }
            else reload += pulseEfficiency() * Time.delta;
        }

        public void addPulseAdjacent(){
            proximity().each(l -> {
                if (pulseModule.pulse > 0 && l instanceof PulseBlockBuild) {
                    float energyTransmitted = Math.min(pulseModule.pulse, energyTransmission);
                    if (((PulseBlockBuild) l).canReceivePulse(energyTransmitted, this) && ((PulseBlockBuild) l).chargef() < chargef()) {
                        ((Pulsec) l).receivePulse(energyTransmitted, this);
                        removePulse(energyTransmitted);
                    }
                }
            });
        }
    }
}
