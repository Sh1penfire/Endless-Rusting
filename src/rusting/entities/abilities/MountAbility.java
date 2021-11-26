package rusting.entities.abilities;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import rusting.EndlessRusting;

public class MountAbility extends Ability {
    public String mountName;
    public float range = 50;
    public float health = 5;
    public float reload = 60;
    public float lineThickness = 4;
    public float laserOffset = 3;
    public float maxWidth = 1.5f;

    public float x = 0, y = 0;

    public boolean mirror;
    public boolean mirrorRotation = false;

    protected float timer;
    private float angle = 0;

    //Purely for ai
    public boolean checkGreaterRange(Ability inputAbility){
        return inputAbility instanceof MountAbility ? ((MountAbility) inputAbility).range > range : false;
    }

    public float angle(){
        return angle;
    }

    public void angle(float rotation){
        angle = rotation;
    }

    public float mountx(Unit unit){
        return Angles.trnsx(unit.rotation - 90, x, y);
    }

    public float mountx(Unit unit, float reverse){
        return Angles.trnsx(unit.rotation - 90, x * reverse, y);
    }

    public float mounty(Unit unit){
        return Angles.trnsy(unit.rotation - 90, x, y);
    }

    public float mounty(Unit unit, float reverse){
        return Angles.trnsy(unit.rotation - 90, x * reverse, y);
    }

    @Override
    public void draw(Unit unit){
        for(int i = 0; i < 2; i++){
            if(!mirror && i > 1) break;
            drawMount(unit, mirrorRotation ? angle * (1 - i * 2) : angle, 1 - i * 2);
        }
    }

    public void drawMount(Unit unit, float angle, float reverse){

            float mountx = mountx(unit, reverse);
            float mounty = mounty(unit, reverse);

            Draw.z(Mathf.lerp(Layer.groundUnit, Layer.flyingUnit, unit.elevation));

            TextureRegion mount = Core.atlas.find(EndlessRusting.modname + "-" + mountName);
            if (Core.atlas.isFound(mount)) Draw.rect(mount, unit.x + mountx, unit.y + mounty,mount.width/4 * reverse, mount.height/4, angle);
            drawLaser(unit, mountx, mounty);
    }

    //open for a beam that could be drawn
    public void drawLaser(Unit unit, float mountx, float mounty){

    }

}
