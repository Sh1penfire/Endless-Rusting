package rusting.type.weather;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.gen.WeatherState;
import rusting.content.Fxr;

public class PowerDisablingParticleWeather extends BlindingParticleWeather{

    public float damage = 1;

    public PowerDisablingParticleWeather(String name) {
        super(name);
    }

    @Override
    public void update(WeatherState state) {
        super.update(state);
        Groups.build.each(b -> {
            if(!b.block.hasPower) return;
            //larger blocks more likely to spark
            if(Mathf.chance(b.block.size/ Vars.maxBlockSize * 0.08f)) Fxr.blueSpark.at(b.x + Mathf.range(8), b.y + Mathf.range(8));
            b.applySlowdown(0, 5);
        });
    }
}
