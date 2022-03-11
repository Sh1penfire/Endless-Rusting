package rusting.world.blocks.pulse.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.Vars;
import mindustry.graphics.Layer;
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
    //how fast rotato
    public float rotationSpeed = 3;

    public TextureRegion rotatorRegion, shineRegion, topRegion;

    public ConductivePulseBlock(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        rotatorRegion = Core.atlas.find(name + "-rotator", region);
        shineRegion = Core.atlas.find(name + "-shine", Core.atlas.find("clear"));
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find("clear"));
    }

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.reload, reloadTime * 60);
    }

    public class ConductivePulseBlockBuild extends PulseBlock.PulseBlockBuild {
        public float reload = 0;
        private float visualRotation = 0;

        @Override
        public void updateTile(){
            super.updateTile();
            if(reload >= reloadTime){
                reload = 0;
                addPulseAdjacent();
            }
            else reload += pulseEfficiency() * Time.delta;
            visualRotation = (visualRotation + Time.delta * rotationSpeed * chargef()) % 360;
        }

        public void addPulseAdjacent(){
            proximity().each(l -> {
                if (storage.pulse > 0 && l instanceof PulseBlockBuild) {
                    float energyTransmitted = Math.min(storage.pulse, energyTransmission);
                    if (((PulseBlockBuild) l).canReceivePulse(energyTransmitted, this) && ((PulseBlockBuild) l).chargef() < chargef()) {
                        ((Pulsec) l).receivePulse(energyTransmitted, this);
                        removePulse(energyTransmitted);
                    }
                }
            });
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y, 0);
            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(rotatorRegion, x, y, visualRotation);
            Draw.rect(topRegion, x, y, 0);
            Draw.alpha(0.25f * Vars.state.rules.ambientLight.a);
            Draw.rect(shineRegion, x, y, 0);
        }
    }
}
