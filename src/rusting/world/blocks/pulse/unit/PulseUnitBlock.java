package rusting.world.blocks.pulse.unit;

import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.meta.BlockGroup;
import rusting.world.blocks.pulse.production.PulsePayloadAcceptor;

public class PulseUnitBlock extends PulsePayloadAcceptor {

    public PulseUnitBlock(String name){
        super(name);
        group = BlockGroup.units;
        outputsPayload = true;
        rotate = true;
        update = true;
        solid = true;
    }

    public class PulseUnitBuild extends PulsePayloadAcceptorBuild<UnitPayload>{
        public float progress, time, speedScl;

    }
}
