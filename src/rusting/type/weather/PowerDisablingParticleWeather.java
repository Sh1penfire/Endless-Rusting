package rusting.type.weather;

import mindustry.gen.Groups;
import mindustry.gen.WeatherState;

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
            b.timeScale = 0;
            b.timeScaleDuration = 100;
        });
    }
}
