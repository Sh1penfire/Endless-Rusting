package rusting.world.blocks.pulse.distribution;

import mindustry.world.meta.Stat;

//preferably used for batteries since syphons are unlocked later
public class PulseSiphon extends PulseNode {
    //How much energy is syphoned
    public float siphonAmount = 1;

    public PulseSiphon(String name) {
        super(name);
        connectable = false;
        //preferably enough to siphon out 2-3 blocks * size.
        energyTransmission = siphonAmount * size * 3;
    }

    @Override
    public void setStats() {
        super.setStats();
        this.stats.add(Stat.reload, pulseReloadTime * 60);
    }

    @Override
    public void setPulseStats(){
        super.setPulseStats();
        pStats.siphonAmount.setValue(siphonAmount);
    }

    public class PulseSiphonBuild extends PulseNodeBuild {

        @Override
        public void interactConnected(){
            super.interactConnected();
            syphonPulseAdjacent();
        }

        public void syphonPulseAdjacent() {
            proximity().each(l -> {
                if (l instanceof PulseBlockBuild && ((PulseBlockBuild) l).chargef() > 0) {
                    float energyTransmitted = Math.min(((PulseBlockBuild) l).pulseModule.pulse, siphonAmount);
                    if (canReceivePulse(energyTransmitted, this) && ((PulseBlockBuild) l).chargef() > chargef()) {
                        ((PulseBlockBuild) l).removePulse(energyTransmitted, this);
                        addPulse(energyTransmitted);
                    }
                }
            });
        }
    }
}
