package rusting.content;

import arc.graphics.Color;

import mindustry.type.Liquid;

public class RustingLiquids {

    static Liquid
            melomae, cameaint;

    
    public static void load() {
        melomae = new Liquid("melomae"){{
            viscosity = 0.7f;
            heatCapacity = 0.325f;
            effect = RustingStatusEffects.macrosis;
            color = Palr.pulseChargeStart;
            barColor = Color.sky;
        }};

        cameaint = new Liquid("cameaint"){{
            viscosity = 0.9f;
            heatCapacity = 0.73f;
            temperature = 0.45f;
            effect = RustingStatusEffects.causticBurning;
            color = Palr.camaintLiquid;
            barColor = Palr.camaintLiquid;
        }};
    }
}
