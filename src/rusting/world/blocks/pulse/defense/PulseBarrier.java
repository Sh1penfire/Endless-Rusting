package rusting.world.blocks.pulse.defense;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Liquids;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import rusting.content.Palr;
import rusting.graphics.Drawr;
import rusting.world.blocks.pulse.PulseBlock;

public class PulseBarrier extends PulseBlock {

    public float pulseAbsorbMulti = 3;
    public Color saturatedColour = Palr.pulseShieldStart;
    public Color shieldColourStart = Liquids.cryofluid.color, shieldColourEnd = Palr.pulseShieldEnd;
    public float maxShieldAlpha = 0.65f;

    public TextureRegion closedRegion, shineRegion, topRegion;

    public PulseBarrier(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        closedRegion = Core.atlas.find(name + "-closed", region);
        shineRegion = Core.atlas.find(name + "-shine", Core.atlas.find("clear"));
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find("clear"));
    }

    public class PulseBarrierBuild extends PulseBlockBuild{

        public float shieldAlpha = 0;
        public float closed = 1;

        @Override
        public void update() {
            super.update();
            shieldAlpha = Mathf.clamp(shieldAlpha - Time.delta/300, 0, 1);
        }

        @Override
        public boolean collision(Bullet other) {
            if(other.type.pierceBuilding && storage.pulse >= other.type.damage/pulseAbsorbMulti){
                removePulse(other.type.damage/pulseAbsorbMulti);
                other.remove();
                shieldAlpha++;
                return true;
            }
            else return super.collision(other);
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y, 0);

            Draw.z(Layer.blockOver - 1);
            if(pulseRegion != Core.atlas.find("error")) {

                Draw.color(chargeColourStart, chargeColourEnd, chargef());

                Draw.alpha(chargef());
                Draw.rect(pulseRegion, x, y, 270);

                Draw.blend(Blending.additive);

                Draw.alpha(chargef() * visualBlendingAlphaMulti);
                Draw.rect(pulseRegion, x, y, 270);

                if(Core.settings.getBool("settings.er.pulseglare")){
                    Draw.alpha(chargef() * chargef() * 0.5f);
                    Draw.rect(pulseRegion, x, y, pulseRegion.height * 1.5f/4, pulseRegion.width * 1.5f/4, 270);
                }
                Draw.blend();
                Draw.color();
            }

            Draw.z(Layer.blockOver);
            Drawr.drawShine(shineRegion, x, y, 0, 0.25f);
            Draw.alpha(closed);
            Draw.rect(topRegion, x, y, 0);
        }

        private float fin(){
            return fin(1);
        }

        private float fin(float scl){
            return (1 - fout(1)) * scl;
        }

        private float fout(){
            return fout(1);
        }

        private float fout(float scl){
            return shieldAlpha * scl;
        }

        private float fslope(){
            return fslope(1);
        }

        private float fslope(float scl){
            return fslope(scl, 0.5f);
        }

        private float fslope(float scl, float threshold){
            return shieldAlpha > threshold ? fin(scl/threshold) : fout(threshold/scl);
        }

        //smooth fslope, only forks for scl at 1 or under
        private float fsslope(float scl, float threshold){
            return fslope(scl, threshold) * fslope(scl, threshold);
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(closed);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            if(revision == 1) closed = r.f();
        }
    }
}
