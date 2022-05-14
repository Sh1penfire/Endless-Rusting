package rusting.world.blocks.pulse.defense;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
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

    //Max damage resistance when shield is up. Add 1 to get the multiplier.
    public float shieldResistMulti = 0.5f;

    public TextureRegion shieldRegion, closedRegion, shineRegion, topRegion;

    public PulseBarrier(String name) {
        super(name);
    }

    public float shieldRadius = 0;
    public int shieldSides = 4;
    public static Vec2 tmp = new Vec2();
    public static float[] verts;

    @Override
    public void load() {
        super.load();
        closedRegion = Core.atlas.find(name + "-closed", region);
        shineRegion = Core.atlas.find(name + "-shine", Core.atlas.find("clear"));
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find("clear"));
        shieldRegion = Core.atlas.find(name + "-shield", Core.atlas.find("clear"));
    }

    @Override
    public void init() {
        super.init();
        if(shieldRadius == 0) shieldRadius = size * Vars.tilesize + 8;
        verts = new float[shieldSides * 2];
    }

    public class PulseBarrierBuild extends PulseBlockBuild{

        public float shieldAlpha = 0;
        public float shieldRegionAlpha = 0;
        public float closed = 1;

        @Override
        public void update() {
            super.update();
            if(closed == 1 && overloaded()){
                Fx.dooropen.at(x, y, size);
                closed = 0;
            }
            else if(closed == 0 && !overloaded()){
                Fx.doorclose.at(x, y, size);
                closed = 1;
            }

            shieldAlpha = Mathf.clamp(shieldAlpha - Time.delta/300, 0, 1);
            shieldRegionAlpha = Mathf.clamp(shieldRegionAlpha - Time.delta/300, 0, 1);
        }

        @Override
        public boolean collision(Bullet other) {
            //total damage resistance
            float damageResistance = (1 + (shieldResistMulti * overloadf())) * pulseAbsorbMulti;
            if(storage.pulse >= other.type.damage/damageResistance){
                storage.pulse -= other.type.damage/damageResistance;
                other.remove();
                shieldAlpha = 1;
                shieldRegionAlpha = 1;
                return true;
            }
            else {
                //take into account overload shield resist
                this.damage(other.damage() * other.type().buildingDamageMultiplier/(1 + damageResistance/pulseAbsorbMulti));
                if(overloaded()) shieldRegionAlpha = overloadf();
                return true;
            }
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

            for (int i = 0; i < shieldSides * 2; i += 2) {
                tmp.trns(i * 360 / shieldSides, shieldRadius);
                verts[i] = tmp.x;
                verts[i + 1] = tmp.y;
            }

            Drawr.polyLight(x, y, verts, shieldColourStart.a(shieldAlpha), shieldColourEnd.a(shieldAlpha));
            if(overloaded()){
                Draw.alpha(shieldRegionAlpha);
                Draw.rect(shieldRegion, x, y, 0);
            }
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
        public void write(Writes w) {
            super.write(w);
            w.f(closed);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            closed = r.f();
        }
    }
}
