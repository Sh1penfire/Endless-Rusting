package rusting.world.blocks.distribution;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import mindustry.gen.Unit;
import mindustry.world.blocks.distribution.Conveyor;

import static mindustry.Vars.tilesize;

public class FrictionConveyor extends Conveyor {
    private static final float itemSpace = 0.4f;

    public float massNegation = 0.015f;

    public FrictionConveyor(String name) {
        super(name);
    }

    public class FrictionConveyorBuild extends ConveyorBuild{
        @Override
        public void unitOn(Unit unit){

            if(clogHeat > 0.5f || !enabled) return;

            noSleep();

            float mspeed = speed * tilesize * 55f;
            float centerSpeed = 0.1f;
            float centerDstScl = 3f;
            float tx = Geometry.d4x(rotation), ty = Geometry.d4y(rotation);

            float centerx = 0f, centery = 0f;

            if(Math.abs(tx) > Math.abs(ty)){
                centery = Mathf.clamp((y - unit.y()) / centerDstScl, -centerSpeed, centerSpeed);
                if(Math.abs(y - unit.y()) < 1f) centery = 0f;
            }else{
                centerx = Mathf.clamp((x - unit.x()) / centerDstScl, -centerSpeed, centerSpeed);
                if(Math.abs(x - unit.x()) < 1f) centerx = 0f;
            }

            if(len * itemSpace < 0.9f){
                Vec2 speedVec = new Vec2((tx * mspeed + centerx) * delta(), (ty * mspeed + centery) * delta());
                float maxLength = Math.max(speedVec.len(), unit.vel.len());
                unit.impulse(speedVec.x * unit.mass()/(len + 1) * massNegation, speedVec.y * unit.mass()/(len + 1) * massNegation);
                if(unit.vel.len() > maxLength) unit.vel.clamp(0, maxLength);
            }
        }
    }
}
