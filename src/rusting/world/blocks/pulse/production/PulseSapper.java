package rusting.world.blocks.pulse.production;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import rusting.Varsr;
import rusting.interfaces.Pulsec;
import rusting.interfaces.block.PulseCanalc;
import rusting.world.blocks.pulse.PulseBlock;

//saps pulse from the nearest Melonaleum Geode. Alternatively acts as a siphon when upgraded, and boasts offensive capability when upgraded further.
public class PulseSapper extends PulseBlock {

    protected static Vec2 tr = new Vec2(0, 0);

    protected static Seq<Tile> tmppTiles = Seq.with();
    protected static ObjectSet<Building> tmpBuilds;

    //offset from beam to barrel
    public float beamOffset = 0;

    //how much Pulse is harvested per tick
    public float collectSpeed = 0.416f,
    //the speed at which the miner turns
    rotationSpeed = 1.85f;

    //doesn't affect the mount; affects it's ability to send Pulse through canals
    public float reloadTime = 15;
    //affects how much Pulse is distributed per tick
    public float pulsePressure = 3.5f;

    TextureRegion baseRegion;

    public PulseSapper(String name) {
        super(name);
        rotate = false;
        canOverload = false;
        canOverdrive = false;
        resistance = 0;
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
    }

    public class PulseSapperBuild extends PulseBlockBuild implements PulseCanalc {
        public float rotation = 90;
        public Vec2 minePos = new Vec2();
        public Pulsec source;
        public float collectThreshold = 60;
        //configuration of the point
        public int mode = 0;

        public float reload = 0;

        @Override
        public void updateTile() {
            boolean canCollect = pulseModule.pulse + collectSpeed * Time.delta < (pulseStorage);

            if(!canCollect) {
                rotation = (rotation + rotationSpeed) % 360;
                minePos.set(x, y);
            }
            else if(validSource(source)) {
                minePos.set(source.getX(), source.getY());
                rotation = Angles.moveToward(rotation, angleTo(minePos), rotationSpeed * Time.delta);
                float removedPulse = source.removePulse(collectSpeed * Time.delta);
                addPulse(removedPulse);
            }
            else{
                switch (mode){
                    case 0: {
                        if(Varsr.world.geodeTiles.size > 0) {
                            tmppTiles = Varsr.world.geodeTiles.sort(t -> t.dst(this)).copy();//.filter(t -> t.build instanceof Pulsec && ((Pulsec) t.build).pulseModule().pulse > collectThreshold);
                            if(tmppTiles.size > 0) source = (Pulsec) tmppTiles.get(0).build;
                        }
                    }
                }
                if(validSource(source)) minePos.set(source.getX(), source.getY());
                else {
                    minePos.set(x, y);
                    rotation = (rotation + rotationSpeed) % 360;
                }
            }

            //as it goes, ctrl c ctrl v
            if(pulseModule.pulse >= pulsePressure && reload >= reloadTime){
                movePulse();
                reload = 0;
            }else{
                reload += Time.delta;
            }
        }

        public void movePulse() {
            Seq<PulseCanalc> distributeTo = Seq.with();
            for (int i = 0; i < 4; i++) {
                Tile next = Vars.world.tileWorld(x + Tmp.v1.trns(i * 90, 8).x, y + Tmp.v1.y);
                //again, more unreadable code
                if(next.build instanceof PulseCanalc && ((PulseCanalc) next.build).canReceive(this) &&
                        ((PulseCanalc) next.build).canReceive(this)) {
                    distributeTo.add((PulseCanalc) next.build);
                }
            }
            distributeTo.each(build -> {
                build.addPulse(removePulse(pulsePressure/distributeTo.size));
            });
        }

        @Override
        public boolean canConnect(Building b) {
            return b instanceof PulseCanalc;
        }

        @Override
        public boolean canReceive(Building b) {
            return b instanceof PulseCanalc;
        }

        @Override
        public boolean canReceivePulse(float pulse, Pulsec build){
            return build instanceof PulseCanalc && pulse + pulseModule.pulse < pulseStorage + (canOverload ? overloadCapacity : 0);
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);
            Draw.z(Layer.turret);
            Draw.rect(region, x, y, rotation - 90);
            tr.trns(rotation, beamOffset);
            if(source != null) Lines.line(x + tr.x, y + tr.y, minePos.x, minePos.y);
        }

        public boolean validSource(Pulsec source){
            return source instanceof Entityc && ((Entityc) source).isAdded() && source.pulseModule().pulse >= collectThreshold;
        }
    }
}
