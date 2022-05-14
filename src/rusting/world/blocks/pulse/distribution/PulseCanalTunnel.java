package rusting.world.blocks.pulse.distribution;

import arc.Core;
import arc.graphics.g2d.TextureRegion;

//god send help
public class PulseCanalTunnel extends PulseCanal{
    TextureRegion tunnelRegion, topOutputRegion, chargeOutputRegion, baseOutputRegion, shineOutputRegion;

    public PulseCanalTunnel(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        region = Core.atlas.find(name + "-input");
        tunnelRegion = Core.atlas.find(name);

        /*
        //load input regions
        pulseRegion = Core.atlas.find(name + "-input-charged");
        baseRegion = Core.atlas.find(name + "-input-base", region);
        shineRegion = Core.atlas.find(name + "-input-shine", Core.atlas.find("empty"));
        topRegion = Core.atlas.find(name + "-input-top", Core.atlas.find("empty"));
        fullRegion = Core.atlas.find(name + "-input-full", region);
         */

        //load output regions
        chargeOutputRegion = Core.atlas.find(name + "-output-charged");
        baseOutputRegion = Core.atlas.find(name + "-output-base", region);
        shineOutputRegion = Core.atlas.find(name + "-output-shine", Core.atlas.find("empty"));
        topOutputRegion = Core.atlas.find(name + "-output-top", Core.atlas.find("empty"));
    }

    public class PulseCanalTunnelBuild extends PulseCanalBuild{

    }
}
