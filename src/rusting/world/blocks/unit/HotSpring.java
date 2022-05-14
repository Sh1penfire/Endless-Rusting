package rusting.world.blocks.unit;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import rusting.content.Fxr;
import rusting.content.RustingStatusEffects;

//heals units and releases particle effects while doing so
public class HotSpring extends Block {

    public float healthPerSecond = 5;
    public float passiveEffectChance = 0.05f;
    public float unitOnEffectChance = 0.15f;
    public Effect smokeEffect = Fxr.healingWaterSmoke;
    public StatusEffect apply = StatusEffects.none;
    public float applyDuration = 1800;
    public Seq<StatusEffect> washOff = Seq.with(StatusEffects.freezing, StatusEffects.burning, StatusEffects.wet, StatusEffects.sapped, StatusEffects.sporeSlowed, StatusEffects.corroded, RustingStatusEffects.hailsalilty, RustingStatusEffects.shieldShatter);
    //ratio
    public float steamProduced = 55;
    public float craftTime = 35;
    public float steamCap = 500;

    public HotSpring(String name) {
        super(name);
        update = true;
        solid = false;
        hasLiquids = true;
        hasShadow = true;
        group = BlockGroup.units;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("power", entity -> new Bar(() ->
                Core.bundle.get("bar.hotspringsteam"),
                () -> Tmp.c1.set(Pal.lightTrail).lerp(Color.white,
                        ((HotSpringBuild) entity).steam/steamCap),
                () -> Mathf.clamp(((HotSpringBuild) entity).steam/steamCap)
        ));
    }

    public class HotSpringBuild extends Building{
        public float steam = 0;
        public float progress = 0;

        @Override
        public void updateTile() {
            super.updateTile();
            if(canConsume() && steam < steamCap){
                if(progress >= 1){
                    progress %= progress;
                    steam += steamProduced * Time.delta;
                }
                else progress += 1/craftTime * Time.delta;
            }
            if(steam > 0 && Mathf.chance(passiveEffectChance)) smokeEffect.at(x, y, 0);
        }

        @Override
        public boolean shouldConsume() {
            return steam <= steamCap;
        }

        @Override
        public void unitOn(Unit unit) {
            super.unitOn(unit);
            //preferably liquid
            if(steam >= 1){
                unit.heal(healthPerSecond * Mathf.clamp(steam, 0, 1)/60 * Time.delta);
                washOff.each(s -> unit.unapply(s));
                unit.apply(apply, applyDuration);
                if(Mathf.chance(unitOnEffectChance)) smokeEffect.at(x, y, 0);
                steam = Math.max(steam - Time.delta, 0);
            }
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(steam);
            w.f(progress);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            steam = r.f();
            progress = r.f();
        }
    }
}