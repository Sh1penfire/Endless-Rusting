package rusting.world.blocks.pulse.defense;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Liquids;
import mindustry.gen.Bullet;
import rusting.content.Palr;
import rusting.world.blocks.pulse.PulseBlock;

public class PulseBarrier extends PulseBlock {

    public float pulseAbsorbMulti = 3;
    public Color saturatedColour = Palr.pulseShieldStart;
    public Color shieldColourStart = Liquids.cryofluid.color, shieldColourEnd = Palr.pulseShieldEnd;
    public float maxShieldAlpha = 0.65f;

    public PulseBarrier(String name) {
        super(name);
    }

    public class PulseBarrierBuild extends PulseBlockBuild{

        public float shieldAlpha = 0;

        @Override
        public void update() {
            super.update();
            shieldAlpha = Mathf.clamp(shieldAlpha - Time.delta/300, 0, 1);
        }

        @Override
        public boolean collision(Bullet other) {
            if(other.type.pierceBuilding && pulseModule.pulse >= other.type.damage/pulseAbsorbMulti){
                removePulse(other.type.damage/pulseAbsorbMulti);
                other.remove();
                shieldAlpha++;
                return true;
            }
            else return super.collision(other);
        }

        @Override
        public void draw() {
            super.draw();
            float alpha = chargef(true) * fout(maxShieldAlpha) * fout(maxShieldAlpha);
            Fill.light(x, y, 10, (size * 8 + 3) + fin(0.8f) * 6, Tmp.c1.set(shieldColourStart).lerp(saturatedColour, chargef(true)).a(alpha), Tmp.c2.set(shieldColourEnd).a(alpha));
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
    }
}
