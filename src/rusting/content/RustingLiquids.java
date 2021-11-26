package rusting.content;

import arc.graphics.Color;
import mindustry.ctype.ContentList;
import mindustry.type.Liquid;

public class RustingLiquids implements ContentList {

    static Liquid
            melomae, cameaint;

    @Override
    public void load() {
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
