package rusting.type.weather;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.gen.WeatherState;
import mindustry.graphics.Layer;
import rusting.Varsr;

public class BlindingParticleWeather extends BaseParticleWeather {

    private static float opacityG;
    public boolean blindAir = true, blindGround = true;
    public float opacityGroundModifier = 0.15f,  opacityAirModifier = 0.85f, opacityModifier = 1.95f;
    //changeable if necessary, though not recommended
    public float drawLayer = Layer.weather;
    //color of blinding
    public Color blindingColor = color;

    public BlindingParticleWeather(String name) {
        super(name);
        drawNoise = true;
    }

    @Override
    public void drawOver(WeatherState state) {
        super.drawOver(state);
        if(drawNoise && Core.settings.getBool("settings.er.weatherblinding")){
            if(Vars.player.unit().isFlying() && blindAir || !Vars.player.unit().isFlying() && blindGround) {
                opacityG = state.opacity * opacityMultiplier * opacityModifier;
                if(blindGround && blindAir) opacityG *= Mathf.lerp(opacityGroundModifier, opacityAirModifier, Varsr.lerpedPlayerElevation);
                else {
                    if (Vars.player.unit().isFlying() && blindAir) opacityG *= opacityAirModifier;
                    if (!Vars.player.unit().isFlying() && blindGround) opacityG *= opacityGroundModifier;
                }
                Draw.alpha(opacityG);
                Draw.z(drawLayer);
                Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
            }
        }
    }

    @Override
    public boolean isHidden() {
        return false;
    }
}
