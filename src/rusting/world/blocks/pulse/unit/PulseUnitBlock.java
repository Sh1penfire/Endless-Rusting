package rusting.world.blocks.pulse.unit;

import mindustry.annotations.Annotations.Loc;
import mindustry.annotations.Annotations.Remote;
import mindustry.world.Tile;
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

    @Remote(called = Loc.server)
    public static void unitBlockSpawn(Tile tile){
        if(tile == null || !(tile.build instanceof PulseUnitBuild)) return;
        ((PulseUnitBuild) tile.build).spawned();
    }

    public class PulseUnitBuild extends PulsePayloadAcceptorBuild<UnitPayload>{
        public float progress, time, speedScl;

        public void spawned(){
            progress = 0f;
            payload = null;
        }

    }
}
