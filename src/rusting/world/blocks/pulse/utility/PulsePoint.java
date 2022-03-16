package rusting.world.blocks.pulse.utility;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.graphics.Layer;
import rusting.interfaces.Pulsec;
import rusting.world.blocks.pulse.PulseBlock;

//Finds units which can be charged with Pulse, or Buildings which are lacking pulse. Fires a continuous stream of Pulse.
public class PulsePoint extends PulseBlock {

    public float pulseAmount;
    public boolean bursts;
    public float rotateSpeed;
    public float range;

    public TextureRegion base;

    @Override
    public void load() {
        super.load();
        base = Core.atlas.find(name + "-base", Core.atlas.find("block-" + size));
    }

    public PulsePoint(String name) {
        super(name);
        bursts = false;
        pulseAmount = 0.15f;
        rotateSpeed = 4;
        range = 60;
        projectileChanceModifier = 0;
        projectile = null;
    }

    @Override
    public boolean canShoot() {
        return true;
    }

    @Override
    public float projectileRange() {
        return range;
    }

    public class PulsePointBuild extends PulseBlockBuild{
        public Pulsec target;
        public float targetRot = 0;
        public float rotation = 270;

        @Override
        public void update() {
            super.update();
            findTarget();
            if(bursts == false && target != null) {
                turnToTarget();
                target.addPulse(pulseAmount);
                targetRot = angleTo(target);
            }
        }

        public void turnToTarget(){
            rotation = Angles.moveToward(rotation, targetRot, rotateSpeed * pEfficiency());
        }

        public void findTarget(){
            target = (Pulsec) Units.closest(team, x, y, range, u -> u instanceof Pulsec && ((Pulsec) u).chargef() < 1);
        }

        @Override
        public void draw() {

            Draw.z(Layer.block);
            Draw.rect(base, x, y, 0);

            Draw.z(Layer.turret);
            Draw.rect(region, x, y, rotation);

            if(pulseRegion != Core.atlas.find("error")) {
                Draw.color(chargeColourStart, chargeColourEnd, chargef());
                Draw.alpha(alphaDraw);
                Draw.rect(shakeRegion, x + xOffset, y + yOffset, (pulseRegion.width + yOffset)/4, (pulseRegion.height + xOffset)/4, 270);
                Draw.alpha(chargef());
                Draw.rect(pulseRegion, x, y, 270);
                Draw.alpha((float) (chargef() * 0.5));
                Draw.rect(pulseRegion, x, y, (float) (pulseRegion.height * 1.5/4), (float) (pulseRegion.width * 1.5/4), 270);
            }
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(rotation);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            rotation = r.f();
        }
    }

}
