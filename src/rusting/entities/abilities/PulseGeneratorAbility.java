package rusting.entities.abilities;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import rusting.content.Palr;
import rusting.interfaces.Pulsec;
import rusting.world.blocks.pulse.PulseBlock;
import rusting.world.blocks.pulse.PulseBlock.PulseBlockBuild;

public class PulseGeneratorAbility extends MountAbility {
    public float pulse;
    public boolean distributePulse;

    protected float timer;
    private PulseBlockBuild building;
    private float timeSincePulse;

    public PulseGeneratorAbility(){
        mountName = "pulse-point";
        mirror = false;
        distributePulse = true;
    }

    @Override
    public void update(Unit unit) {
        if(validate(building, unit)) {
            angle(unit.angleTo(building) - 90);
        }
        else{
            building = (PulseBlockBuild) Vars.indexer.findTile(unit.team, unit.x, unit.y, range, b -> b instanceof PulseBlockBuild && ((PulseBlockBuild) b).canReceivePulse(pulse, (Pulsec) unit));
            if(timeSincePulse >= 360) {
                //I swear theres an easier way to do this
                float unitRotation = unit.rotation - 90;
                angle(
                    unitRotation - angle() < 0 ?
                    Math.max(angle() - 1, unitRotation) :
                    Math.min(angle() + 1, unitRotation)
                );
            }
            else timeSincePulse++;
        }
        timer += Time.delta;
        if(timer >= reload){
            if(building != null) {
                building.addPulse(pulse);
                timeSincePulse = 0;
            }
            else if(unit instanceof Pulsec) ((Pulsec) unit).addPulse(pulse);
            timer = 0;
        }
    }

    @Override
    public void drawLaser(Unit unit, float mountx, float mounty) {
        if(validate(building, unit)){
            Draw.color(Palr.pulseChargeEnd, Palr.pulseChargeEnd, timer/reload);
            float sourcx = unit.x + mountx + Angles.trnsx(angle(), 0, laserOffset), sourcy = unit.y + mounty + Angles.trnsy(angle(), 0, laserOffset);
            float edgex = building.x + Angles.trnsx(angle() - 180, 0, ((PulseBlock) building.block).laserOffset), edgey = building.y + Angles.trnsy(angle() - 180, 0, ((PulseBlock) building.block).laserOffset);
            float width = Math.min((1 - unit.dst(building.x, building.y) / range) * lineThickness, maxWidth);

            Draw.z(Layer.flyingUnit + 1);
            Lines.stroke(width * 1.35f);
            Lines.line(sourcx, sourcy, edgex, edgey);
            Fill.circle(edgex, edgey, width * 1.35f);
            Fill.circle(sourcx, sourcy, width * 0.85f);
            Draw.reset();
        }
    }

    public boolean validate(PulseBlockBuild checkedBuilding, Unit unit){
        return building != null && building.canReceivePulse(pulse, (Pulsec) unit) && building.within(unit, range) && building.isAdded();
    }

    @Override
    public String localized(){
        return Core.bundle.get("ERability.pulsegenerator");
    }
}