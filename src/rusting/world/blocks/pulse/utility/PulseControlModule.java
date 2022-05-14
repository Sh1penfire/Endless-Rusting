package rusting.world.blocks.pulse.utility;

import rusting.interfaces.PrimitiveControlBlock;
import rusting.world.blocks.pulse.PulseBlock;

//base class for primitive logic system
public class PulseControlModule extends PulseBlock {

    public PulseControlModule(String name) {
        super(name);
    }

    public class PulseControlModuleBuild extends PulseBlockBuild implements PrimitiveControlBlock {

    }
}
