package rusting.world.blocks.pulse;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Effect;
import rusting.Varsr;
import rusting.content.Fxr;
import rusting.interfaces.Pulsec;
import rusting.world.blocks.pulse.distribution.PulseSource.PulseSourceBuild;

public class PulseBoulder extends PulseBlock{

    public Effect passiveEffect = Fxr.healingColdWaterSmoke;
    public float effectChance = 0.15f;
    public float destroyAmount = 55;
    public float pulseProduction = 0;

    public float selfDamage = 3, damageRampup = 4;

    public TextureRegion crystalRegion;

    public PulseBoulder(String name) {
        super(name);
        canOverload = false;
    }

    @Override
    public void load() {
        super.load();
        crystalRegion = Core.atlas.find(name + "-crystals");
    }

    public class PulseBoulderBuild extends PulseBlockBuild{

        @Override
        public void placed() {
            super.placed();
            addPulse();
            Varsr.world.geodeTiles.add(tile);
            Varsr.world.geodeBuildings.add(this);
        }

        @Override
        public void dropped() {
            super.dropped();
            Varsr.world.geodeTiles.add(tile);
            Varsr.world.geodeBuildings.add(this);
        }

        @Override
        public void remove() {
            super.remove();
            Varsr.world.geodeTiles.remove(tile);
            Varsr.world.geodeBuildings.remove(this);
        }

        @Override
        public boolean canReceivePulse(float pulse, Pulsec build) {
            return build instanceof PulseSourceBuild;
        }

        @Override
        public void update() {
            super.update();
            if(Mathf.chance(effectChance * Time.delta)) passiveEffect.at(x + Mathf.range(size * 8), y + Mathf.random(size * 8));
            if(pulseModule.pulse <= destroyAmount) damage(selfDamage * (1 - chargef()) * damageRampup * Time.delta);
            pulseModule.pulse += pulseProduction * Time.delta;
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            Draw.alpha(chargef());
            Draw.rect(crystalRegion, x, y);
        }
    }
}
