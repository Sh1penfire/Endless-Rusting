package rusting.world.blocks.pulse.utility;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.UnitTypes;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.logic.LAccess;
import mindustry.world.blocks.ControlBlock;
import rusting.interfaces.PrimitiveControlBlock;

//block for controlling other Pulse blocks
public class PulseController extends PulseControlModule {

    public TextureRegion pointerRegion, enabledRegion;

    public int faces = 4;

    public PulseController(String name) {
        super(name);
        drawDisabled = false;
        autoResetEnabled = false;
    }

    @Override
    public void load() {
        super.load();
        pointerRegion = Core.atlas.find(name + "-pointer");
        enabledRegion = Core.atlas.find(name + "-enabled");
    }

    public class PulseControllerBuild extends PulseControlModuleBuild implements ControlBlock {

        public boolean isTapping = false;
        public float rotation = 0, tempRotation = 0;
        private final float interval = 360/faces;

        @Override
        public void update() {
            super.update();
            if(unit() != null && isControlled()) {
                tempRotation = unit().angleTo(unit().aimX(), unit().aimY());
                if(unit().isShooting()){
                    if(isTapping != true){
                        Sounds.click.at(x, y);
                        isTapping = true;
                        enabled = !enabled;
                    }
                }
                else if(isTapping) {
                    isTapping = false;
                }
            }


            if(enabled) {
                tempRotation %= 360;
                rotation = interval * Math.round(tempRotation / interval);
            }
        }

        public @Nullable
        BlockUnitc unit;

        @Override
        public void created(){
            unit = (BlockUnitc) UnitTypes.block.create(team);
            unit.tile(this);
        }

        @Override
        public void primitiveControl(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.shoot && (unit == null || !unit.isPlayer())){
                tempRotation = angleTo((float) p1 * 8, (float) p2 * 8);
                enabled = !Mathf.zero(p3);
            }
        }

        @Override
        public void rawControl(double p1, double p2, double p3, double p4) {
            tempRotation = (float) p2;
            rotation = (float) p2;
            enabled = !Mathf.zero(p3);
        }

        public boolean showPointer(){
            return enabled && !(isControlled() && dst(unit.aimX(), unit.aimY()) < size * 4);
        }

        @Override
        public void exportInformationDefault(Building build) {
            if(build instanceof PrimitiveControlBlock) ((PrimitiveControlBlock) build).rawControl(showPointer() ? 1.5F : 0, rotation, enabled ? 1 : 0, 1F);
        }

        @Override
        public Boolean config(){
            return enabled;
        }

        @Override
        public double sense(LAccess sensor) {
            switch(sensor){
                case rotation: {
                    return rotation;
                }
                default: {
                    return super.sense(sensor);
                }
            }
        }

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc)UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }

        @Override
        public void draw() {
            super.draw();
            Draw.alpha(1);
            Draw.z(Layer.effect);
            Draw.color(chargeColourStart, chargeColourEnd, chargef()/5 + 0.8f);
            if(enabled()) Draw.rect(enabledRegion, x, y, 270);
            if(showPointer()) Draw.rect(pointerRegion, x, y, rotation - 90);
        }

        //if one of you dingusese managed to make a block with the controller without me even knowing I swear I will find you and I will ask you to not mess around with unused content
        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void readAll(Reads read, byte revision){
            super.readAll(read, revision);

            if(revision == 1){
                enabled = read.bool();
                tempRotation = read.f();
            }
            unit.remove();
            created();
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(enabled);
            write.f(rotation);
        }

    }

}
