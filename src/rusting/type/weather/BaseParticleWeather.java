package rusting.type.weather;

import mindustry.type.weather.ParticleWeather;

public class BaseParticleWeather extends ParticleWeather {
    public BaseParticleWeather(String name) {
        super(name);
    }

    @Override
    public boolean isHidden() {
        return false;
    }
}
