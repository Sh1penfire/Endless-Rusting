package rusting.content;

import arc.graphics.Color;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.ctype.ContentList;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.type.Weather;
import mindustry.world.meta.Attribute;
import rusting.type.weather.*;

public class RustingWeathers implements ContentList{
    public static Weather
            //destructive
            fossilStorm, corrosiveDeluge, pulesweptGround, chemNullificationStorm, hailsiteSpray;

    @Override
    public void load(){
        fossilStorm = new BulletParticleWeather("fossil-storm"){{
            particleBullet = RustingBullets.fossilShard;
            dynamicSpawning = true;
            chanceSpawn = 4;
            randRange = new Vec2(4, 4);
            color = noiseColor = Color.valueOf("#c4cf6f");
            particleRegion = "particle";
            drawNoise = true;
            useWindVector = true;
            sizeMax = 8;
            sizeMin = 4;
            minAlpha = 0.1f;
            maxAlpha = 0.8f;
            density = 1850;
            baseSpeed = 3.45f;
            opacityMultiplier = 0.45f;
            force = 0.15f;
            sound = Sounds.wind;
            soundVol = 0.7f;
            duration = 2 * Time.toMinutes;
            attrs.set(Attribute.light, -0.4f);
            attrs.set(Attribute.water, -0.2f);
        }};

        corrosiveDeluge = new BlindingParticleWeather("corrosive-deluge") {{
            color = noiseColor = Color.coral;
            useWindVector = true;
            drawNoise = true;
            sizeMax = 0;
            sizeMin = 0;
            status = StatusEffects.corroded;
            statusGround = false;
            statusDuration = 125f;
            opacityMultiplier = 0.35f;
            force = 0.075f;
            sound = Sounds.rain;
            soundVol = 0.45f;
            duration = 6.35f * Time.toMinutes;
            drawLayer = Layer.overlayUI;
            attrs.set(Attribute.light, 0.75f);
            attrs.set(Attribute.water, 0.35f);
        }};

        pulesweptGround = new BlindingParticleWeather("pulseswept-ground"){{
            color = Palr.pulseBullet;
            blindingColor = Palr.darkerPulseChargeStart;
            drawNoise = true;
            sizeMax = 5;
            sizeMin = 2;
            minAlpha = 0.2f;
            maxAlpha = 0.6f;
            density = 1550;
            baseSpeed = 1.25f;
            opacityMultiplier = 0.375f;
            drawLayer = Layer.groundUnit + 0.5f;
            status = RustingStatusEffects.balancedPulsation;
            statusAir = false;
            useWindVector = true;
            force = 0.15f;
            sound = Sounds.windhowl;
            duration = 2.45f * Time.toMinutes;
            opacityModifier = 5;
            opacityGroundModifier = 0.35f;
            opacityAirModifier = 0.05f;
            attrs.set(Attribute.light, -0.95f);
            attrs.set(Attribute.water, -0.55f);
            attrs.set(Attribute.spores, -1);
            attrs.set(Attribute.oil, -0.15f);
            attrs.set(Attribute.heat, -0.35f);
        }};

        chemNullificationStorm = new BaseParticleWeather("chem-nullification-storm") {{
            color = noiseColor = Color.cyan;
            particleRegion = "particle";
            useWindVector = true;
            sizeMax = 8;
            sizeMin = 4;
            minAlpha = 0.1f;
            maxAlpha = 0.8f;
            density = 1850;
            baseSpeed = 3.45f;
            status = RustingStatusEffects.causticBurning;
            statusDuration = 500f;
            opacityMultiplier = 0.45f;
            force = 0.035f;
            sound = Sounds.rain;
            soundVol = 0.45f;
            duration = 4.35f * Time.toMinutes;
            attrs.set(Attribute.light, 0.75f);
            attrs.set(Attribute.water, 0.35f);
            attrs.set(Attribute.heat, -0.15f);
            attrs.set(Attribute.oil, -0.25f);
            attrs.set(Attribute.spores, -0.45f);
        }};

        hailsiteSpray = new BaseParticleWeather("hailsite-spray"){{
            color = noiseColor = Palr.lightstriken;
            particleRegion = "particle";
            statusGround = false;
            useWindVector = true;
            status = RustingStatusEffects.hailsalilty;
            sizeMax = 6f;
            sizeMin = 1.8f;
            minAlpha = 0.5f;
            maxAlpha = 1f;
            density = 10000f;
            baseSpeed = 0.09f;
        }};
    }
}
