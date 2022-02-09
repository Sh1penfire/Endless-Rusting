package rusting.type.weather;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Groups;
import mindustry.gen.WeatherState;

public class BulletDamagingParticleWeather extends BaseParticleWeather {
    //damage to all bullets on screen per tick
    public float damage = 0.45f;

    //bullet lifetime damage dealt each tick. Unless the bullet is unhittable, this is unpreventable
    public float lifetimeRemovalPercentage = 0.01f;

    public Effect bulletDamageEffect = Fx.plasticburn;
    public float effectChance = 0.05f;

    public BulletDamagingParticleWeather(String name) {
        super(name);
    }

    @Override
    public void update(WeatherState state) {
        super.update(state);
        Groups.bullet.each(b -> {
            if(b.type.hittable) {
                b.damage -= damage * Time.delta;
                b.time += b.lifetime * lifetimeRemovalPercentage;
                if(Mathf.chance(effectChance)) bulletDamageEffect.at(b.x, b.y, b.rotation());
                if(b.damage <= 0) {
                    bulletDamageEffect.at(b.x, b.y, b.rotation());
                    b.type.despawned(b);
                    b.remove();
                }
            }
        });
    }
}
