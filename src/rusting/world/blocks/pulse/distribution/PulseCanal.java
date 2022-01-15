package rusting.world.blocks.pulse.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import rusting.interfaces.*;
import rusting.interfaces.block.PulseCanalc;
import rusting.world.blocks.pulse.PulseBlock;
import rusting.world.blocks.pulse.utility.PulseTeleporterController.PulseTeleporterControllerBuild;

//Todo: this is a mess and I regret most things
public class PulseCanal extends PulseBlock {
    public TextureRegion[] topRegion, baseRegion, shineRegion, fullRegion;

    public float pulsePressure;

    public float reloadTime = 1;

    public PulseCanal(String name) {
        super(name);
        rotate = true;
        schematicPriority = 10;
        resistance = 0;
        connectable = false;
        liquidCapacity = 16f;
        liquidPressure = 1.025f;
        pulsePressure = 10f;
        pulseStorage = 15;
        hasLiquids = true;
    }

    @Override
    public void load() {
        super.load();
        //Todo: make all of this a single Array
        baseRegion = new TextureRegion[]{
                Core.atlas.find(name + "-base", region),
                Core.atlas.find(name + "-input-base", region)
        };
        shineRegion = new TextureRegion[]{
                Core.atlas.find(name + "-shine", region),
                Core.atlas.find(name + "-input-shine", region)
        };
        topRegion = new TextureRegion[]{
                Core.atlas.find(name + "-top", region),
                Core.atlas.find(name + "-input-top", region)
        };
        fullRegion = new TextureRegion[]{
                Core.atlas.find(name + "-full", region),
                Core.atlas.find(name + "-input-full", region)
        };
    }

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.rect(baseRegion[0], req.drawx(), req.drawy(), req.rotation * 90);

        Draw.alpha(0.15f);
        Draw.rect(shineRegion[0], req.drawx(), req.drawy(), 270);
        Draw.alpha(1);
        Draw.z(Layer.blockOver + 0.1f);

        Draw.rect(topRegion[0], req.drawx(), req.drawy(), req.rotation * 90);
    }

    public static PulseCanalBuild asCanal(Building build){
        return (PulseCanalBuild) build;
    }

    private PulseTeleporterControllerBuild build;
    private static Building tmpBuild;
    private static Tile tmpTile = null;
    private static PulseCornerpiece corner = null;

    public class PulseCanalBuild extends PulseBlockBuild implements PulseCanalc {
        public float smoothPulse = 0;
        public float reload = 0;

        public Tile canalInput = tile;
        public Tile canalEnding = tile;

        public int input = 1;

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            canalEnding = next();
            canalInput = behind();
            input = canalInput == null ? 1 : 0;
        }

        @Override
        public void updateTile(){
            if(canalEnding != null){
                if(pulseModule.pulse >= moveAmount() && reload >= reloadTime){
                    for (float i = reloadTime; i < reload; i++) {
                        movePulse();
                        reload -= 1;
                    }
                }else{
                    reload += Time.delta;
                }
            }
            smoothPulse = Mathf.lerpDelta(smoothPulse, chargef(), 0.05f);
        }

        public float moveAmount(){
            return (pulseStorage + (canOverload ? overloadCapacity : 0)) * pulsePressure/60 * Time.delta * chargef();
        }

        public void movePulse() {
            Building next = canalEnding.build;
            if(next != null && ((PulseCanalc) next).receivePulse(moveAmount(), this)) {
                removePulse(moveAmount());
            }
        }

        public Tile behind(){
            Tile behind = tile.nearby((rotation + 2) % 4);
            if(behind.build instanceof PulseCanalc && ((PulseCanalc) behind.build).canConnect(this) && canReceive(behind.build)){
                return behind;
            }
            return null;
        }

        public Tile next(){
            Tile next = tile.nearby(rotation);
            if(next.build instanceof PulseCanalc && canConnect(next.build) && ((PulseCanalc) next.build).canReceive(this)){
                return next;
            }
            return null;
        }

        public boolean adjacentRotational(Tile t){
            return ((int) (t.build.angleTo(this)/90) + 1) % 2 == (rotation + 1) % 2;
        }

        public boolean behindRotational(Tile t){
            return ((int) t.build.angleTo( this)/90) == rotation;
        }

        @Override
        public boolean canConnect(Building b){
            return b instanceof PulseCanalc && (!b.block.rotate || b.rotation == rotation && adjacentRotational(b.tile));
        }

        @Override
        public boolean connectableTo() {
            return input == 1;
        }

        @Override
        public boolean canReceive(Building b){
            return b instanceof Pulsec && behindRotational(b.tile) || input == 1;
        }

        @Override
        public boolean canReceivePulse(float pulse, Pulsec build) {
            return super.canReceivePulse(pulse, build) && (build instanceof PulseCanalc || build == this) && canReceive(((PulseCanalc) build).tile().build);
        }
        //Vars.world.buildWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y).setupEnding()

        @Override
        public void draw() {
            Draw.rect(baseRegion[input], x, y, rotation * 90);
            Draw.color(chargeColourStart, chargeColourEnd, smoothPulse);
            Draw.alpha(smoothPulse);
            Draw.rect(pulseRegion, x, y, rotation * 90);
            Draw.color();
            Draw.alpha(0.15f);
            Draw.rect(shineRegion[input], x, y, 270);
            Draw.alpha(1);
            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion[input], x, y, rotation * 90);
        }
    }
}
