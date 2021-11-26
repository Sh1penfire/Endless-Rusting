package rusting.entities.abilities;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

public class HealthEqualizerAbility extends MountAbility{

    protected float retargetTime = 35;
    protected float ret;
    private float timeSinceHeal = 0;
    private Unit ally;

    public HealthEqualizerAbility(){
        mountName = "siphon-point";
    }

    @Override
    public void update(Unit unit) {

        super.update(unit);

        //increase reload while ally is valid and angle repair points towards unit. If retarget retarget.
        if(!(ret >= retargetTime) && validate(ally, unit) && ally.damaged()){
            angle(ally.angleTo(unit.x, unit.y) + 90);
            timer = Math.min(timer + Time.delta, reload);
            timeSinceHeal = 0;
        }
        else {
            //retarget, or invalidate target
            ret = 0;
            Unit TempUnit = findAlly(unit);
            if(TempUnit != null) ally = TempUnit;
            else ally = null;
            if(timeSinceHeal >= 360) angle(angle() + 1);
            else timeSinceHeal++;
        }

        //retarget time increased
        ret = Math.min(ret + Time.delta, retargetTime);

        //actualy "fire" the mount, causing the unit to take damage equivalent to how much it's heal it's targeted ally
        if (timer >= reload) {
            //don't kill the unit while healing
            if (ally != null && ally.healthf() < unit.healthf() && Math.min(ally.maxHealth - ally.health, health) < unit.health) {
                float healAmount = Math.min(ally.maxHealth - ally.health, health);
                ally.heal(healAmount);
                unit.health -= healAmount;
            }
            else if(ally == null || !ally.damaged()) ally = null;
            timer = 0;
        }
    }

    public void drawLaser(Unit unit, float mountx, float mounty){
        if(validate(ally, unit)){

            float sourcx = unit.x + mountx + Angles.trnsx(angle(), 0, laserOffset), sourcy = unit.y + mounty + Angles.trnsy(angle(), 0, laserOffset);
            float edgex = ally.x, edgey = ally.y;
            float width = Math.min((1 - unit.dst(ally.x, ally.y) / range) * lineThickness, maxWidth);

            Draw.z(Layer.flyingUnit + 1);

            Draw.color(Pal.lightTrail, Pal.heal, timer / reload);
            Lines.stroke(width * 1.35f);
            Lines.line(sourcx, sourcy, edgex, edgey);
            Fill.circle(edgex, edgey, width * 1.35f);
            Fill.circle(sourcx, sourcy, width * 0.85f);
            Draw.reset();
        }
    }

    public Unit findAlly(Unit unit){
        Unit closestDamaged = Units.closest(unit.team, unit.x, unit.y, range, u -> u != unit && u.healthf() <= unit.healthf() && u.damaged());
        if(validate(closestDamaged, unit) && (ally != null ? (unit.dst(closestDamaged.x, closestDamaged.y) < unit.dst(ally.x, ally.y) || !ally.damaged() && closestDamaged.damaged() || closestDamaged.healthf() < ally.healthf()) : true)) return closestDamaged;
        return null;
    }

    public boolean validate(Teamc alliedUnit, Unit unit){
        return alliedUnit != unit && alliedUnit != null && alliedUnit.within(unit, range) && alliedUnit.isAdded();
    }

    @Override
    public String localized() {
        return Core.bundle.get("ERability.healthequalizer");
    }
}